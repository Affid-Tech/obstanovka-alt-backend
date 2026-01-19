package com.example.facilities.domain.service

import com.example.facilities.api.dto.CapabilityDetailsDTO
import com.example.facilities.api.dto.CityRefDTO
import com.example.facilities.api.dto.ContactPointDTO
import com.example.facilities.api.dto.CoordinatesDTO
import com.example.facilities.api.dto.EquipmentCategoryDTO
import com.example.facilities.api.dto.EquipmentItemDTO
import com.example.facilities.api.dto.FacilityCapabilityDTO
import com.example.facilities.api.dto.FacilityCardDTO
import com.example.facilities.api.dto.FacilityDetailsDTO
import com.example.facilities.api.dto.FeatureValueDTO
import com.example.facilities.api.dto.MediaItemDTO
import com.example.facilities.api.dto.OpeningHoursDTO
import com.example.facilities.api.dto.PriceHintDTO
import com.example.facilities.api.dto.SpaceDTO
import com.example.facilities.domain.repo.FacilityRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Service
class FacilityService(
    private val facilityRepository: FacilityRepository
) {
    @Transactional(readOnly = true)
    fun listFacilities(
        cityId: UUID,
        query: String?,
        capability: List<String>?,
        feature: List<String>?,
        equipmentCategory: List<String>?,
        spaceType: List<String>?,
        hasAddress: Boolean?,
        hasCoordinates: Boolean?,
        priceMin: BigDecimal?,
        priceMax: BigDecimal?,
        sort: String,
        page: Int,
        pageSize: Int
    ): Pair<List<FacilityCardDTO>, Long> {
        val capabilityCodes = normalizeCodes(capability)
        val featureCodes = normalizeCodes(feature)
        val equipmentCategories = normalizeCodes(equipmentCategory)
        val spaceTypes = normalizeCodes(spaceType)

        val ids = facilityRepository.findFacilityIds(
            cityId = cityId,
            query = query?.trim()?.takeIf { it.isNotBlank() },
            capabilityCodes = capabilityCodes,
            featureCodes = featureCodes,
            equipmentCategories = equipmentCategories,
            spaceTypes = spaceTypes,
            hasAddress = hasAddress,
            hasCoordinates = hasCoordinates,
            priceMin = priceMin,
            priceMax = priceMax,
            sort = sort,
            page = page,
            pageSize = pageSize
        )

        val total = facilityRepository.countFacilities(
            cityId = cityId,
            query = query?.trim()?.takeIf { it.isNotBlank() },
            capabilityCodes = capabilityCodes,
            featureCodes = featureCodes,
            equipmentCategories = equipmentCategories,
            spaceTypes = spaceTypes,
            hasAddress = hasAddress,
            hasCoordinates = hasCoordinates,
            priceMin = priceMin,
            priceMax = priceMax
        )

        if (ids.isEmpty()) return emptyList<FacilityCardDTO>() to total

        val baseRows = facilityRepository.fetchFacilitiesBase(ids).associateBy { it.id }
        val capabilities = facilityRepository.fetchCapabilities(ids).groupBy { it.facilityId }
        val featureCodesMap = facilityRepository.fetchFeatureCodes(ids)
            .groupBy({ it.first }, { it.second })
        val equipmentCategoryMap = facilityRepository.fetchEquipmentCategories(ids)
            .groupBy({ it.first }, { it.second })
        val priceInfoMap = facilityRepository.fetchPriceInfo(ids).groupBy { it.facilityId }

        val cards = ids.mapNotNull { id ->
            val row = baseRows[id] ?: return@mapNotNull null
            FacilityCardDTO(
                id = row.id,
                name = row.name,
                city = CityRefDTO(row.cityId, row.cityName),
                addressLabel = row.addressLabel,
                coordinates = if (row.lat != null && row.lng != null) CoordinatesDTO(row.lat, row.lng) else null,
                coverImageUrl = row.coverImageUrl,
                capabilities = capabilities[id].orEmpty().map { FacilityCapabilityDTO(it.code, it.label) },
                priceHint = buildPriceHint(priceInfoMap[id].orEmpty()),
                featureCodes = featureCodesMap[id].orEmpty().distinct().sorted(),
                equipmentCategoryCodes = equipmentCategoryMap[id].orEmpty().distinct().sorted()
            )
        }

        return cards to total
    }

    @Transactional(readOnly = true)
    fun getFacilityDetails(facilityId: UUID): FacilityDetailsDTO? {
        val base = facilityRepository.fetchFacilityBase(facilityId) ?: return null
        val capabilities = facilityRepository.fetchCapabilities(listOf(facilityId)).map {
            FacilityCapabilityDTO(it.code, it.label)
        }
        val featureCodes = facilityRepository.fetchFeatureCodes(listOf(facilityId)).map { it.second }.distinct().sorted()
        val equipmentCategoryCodes = facilityRepository.fetchEquipmentCategories(listOf(facilityId)).map { it.second }.distinct().sorted()
        val priceHint = buildPriceHint(facilityRepository.fetchPriceInfo(listOf(facilityId)))
        val gallery = facilityRepository.fetchGallery(facilityId).map { MediaItemDTO(it.url, it.caption, it.sortOrder) }
        val contacts = facilityRepository.fetchContacts(facilityId).map { ContactPointDTO(it.type, it.value, it.label, it.isPrimary) }
        val openingHours = facilityRepository.fetchOpeningHours(facilityId).map {
            OpeningHoursDTO(it.dayOfWeek, it.isClosed, it.openTime, it.closeTime, it.note)
        }
        val capabilityDetails = facilityRepository.fetchCapabilityDetails(facilityId).map {
            CapabilityDetailsDTO(it.code, it.label, it.summary, it.details)
        }

        val spaces = facilityRepository.fetchSpaces(facilityId)
        val spaceMedia = facilityRepository.fetchSpaceMedia(spaces.map { it.id }).groupBy { it.spaceId }
        val spaceDtos = spaces.map { space ->
            SpaceDTO(
                id = space.id,
                name = space.name,
                typeCode = space.typeCode,
                capacityPeople = space.capacityPeople,
                sizeM2 = space.sizeM2,
                description = space.description,
                media = spaceMedia[space.id].orEmpty().map { MediaItemDTO(it.url, it.caption, it.sortOrder) }
            )
        }

        val equipmentRows = facilityRepository.fetchEquipment(facilityId)
        val equipment = equipmentRows.groupBy { it.categoryCode }.map { (category, items) ->
            EquipmentCategoryDTO(
                category = category,
                items = items.map { EquipmentItemDTO(it.name, it.quantity, it.mode, it.note) }
            )
        }

        val features = facilityRepository.fetchFeatures(facilityId).map { row ->
            FeatureValueDTO(
                code = row.code,
                label = row.label,
                value = row.valueBool ?: row.valueNumber ?: row.valueText
            )
        }

        return FacilityDetailsDTO(
            id = base.id,
            name = base.name,
            city = CityRefDTO(base.cityId, base.cityName),
            addressLabel = base.addressLabel,
            coordinates = if (base.lat != null && base.lng != null) CoordinatesDTO(base.lat, base.lng) else null,
            coverImageUrl = base.coverImageUrl,
            capabilities = capabilities,
            priceHint = priceHint,
            featureCodes = featureCodes,
            equipmentCategoryCodes = equipmentCategoryCodes,
            description = base.description,
            gallery = gallery,
            contacts = contacts,
            openingHours = openingHours,
            capabilityDetails = capabilityDetails,
            spaces = spaceDtos,
            equipment = equipment,
            features = features
        )
    }

    private fun normalizeCodes(codes: List<String>?): List<String> = codes
        ?.map { it.trim().uppercase() }
        ?.filter { it.isNotBlank() }
        ?.distinct()
        ?: emptyList()

    private fun buildPriceHint(rows: List<FacilityRepository.PriceInfoRow>): PriceHintDTO? {
        if (rows.isEmpty()) return null
        val numeric = rows.filter { it.kind != "CONTACT" && (it.amountFrom != null || it.amountTo != null) }
        val bestNumeric = numeric.minByOrNull { it.amountFrom ?: it.amountTo ?: BigDecimal.ZERO }
        if (bestNumeric != null) {
            return PriceHintDTO(
                kind = bestNumeric.kind,
                amountFrom = bestNumeric.amountFrom,
                amountTo = bestNumeric.amountTo,
                currency = bestNumeric.currency,
                note = bestNumeric.note
            )
        }
        val contact = rows.firstOrNull { it.kind == "CONTACT" }
        return contact?.let {
            PriceHintDTO(kind = it.kind, amountFrom = it.amountFrom, amountTo = it.amountTo, currency = it.currency, note = it.note)
        }
    }
}
