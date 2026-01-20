package com.example.facilities.domain.service

import com.example.facilities.api.admin.dto.AdminContactPointCreate
import com.example.facilities.api.admin.dto.AdminFacility
import com.example.facilities.api.admin.dto.AdminFacilityCapability
import com.example.facilities.api.admin.dto.AdminFacilityCreate
import com.example.facilities.api.admin.dto.AdminFacilityEquipment
import com.example.facilities.api.admin.dto.AdminFacilityFeature
import com.example.facilities.api.admin.dto.AdminFacilityMedia
import com.example.facilities.api.admin.dto.AdminFacilityUpdate
import com.example.facilities.api.admin.dto.AdminOpeningHours
import com.example.facilities.api.admin.dto.AdminPriceInfoCreate
import com.example.facilities.domain.repo.AdminAddressRepository
import com.example.facilities.domain.repo.AdminContactPointRepository
import com.example.facilities.domain.repo.AdminFacilityCapabilityRepository
import com.example.facilities.domain.repo.AdminFacilityEquipmentRepository
import com.example.facilities.domain.repo.AdminFacilityFeatureRepository
import com.example.facilities.domain.repo.AdminFacilityMediaRepository
import com.example.facilities.domain.repo.AdminFacilityRepository
import com.example.facilities.domain.repo.AdminOpeningHoursRepository
import com.example.facilities.domain.repo.AdminPriceInfoRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalTime
import java.util.UUID

@Service
class AdminFacilityService(
    private val adminFacilityRepository: AdminFacilityRepository,
    private val adminAddressRepository: AdminAddressRepository,
    private val adminContactPointRepository: AdminContactPointRepository,
    private val adminOpeningHoursRepository: AdminOpeningHoursRepository,
    private val adminFacilityCapabilityRepository: AdminFacilityCapabilityRepository,
    private val adminFacilityFeatureRepository: AdminFacilityFeatureRepository,
    private val adminFacilityEquipmentRepository: AdminFacilityEquipmentRepository,
    private val adminFacilityMediaRepository: AdminFacilityMediaRepository,
    private val adminPriceInfoRepository: AdminPriceInfoRepository,
    private val objectMapper: ObjectMapper
) {
    @Transactional(readOnly = true)
    fun getFacility(id: UUID): AdminFacility {
        val row = adminFacilityRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Facility not found")
        return toAdminFacility(row)
    }

    @Transactional
    fun createFacility(request: AdminFacilityCreate): AdminFacility {
        validateAddressCity(request.cityId, request.addressId)
        val id = UUID.randomUUID()
        adminFacilityRepository.insert(
            AdminFacilityRepository.FacilityRow(
                id = id,
                cityId = request.cityId,
                name = request.name,
                description = request.description,
                addressId = request.addressId,
                coverMediaId = request.coverMediaId,
                status = request.status
            )
        )
        return getFacility(id)
    }

    @Transactional
    fun updateFacility(id: UUID, request: AdminFacilityUpdate): AdminFacility {
        val existing = adminFacilityRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Facility not found")
        val cityId = request.cityId ?: existing.cityId
        val addressId = request.addressId ?: existing.addressId
        validateAddressCity(cityId, addressId)
        val updated = AdminFacilityRepository.FacilityRow(
            id = existing.id,
            cityId = cityId,
            name = request.name ?: existing.name,
            description = request.description ?: existing.description,
            addressId = addressId,
            coverMediaId = request.coverMediaId ?: existing.coverMediaId,
            status = request.status ?: existing.status
        )
        adminFacilityRepository.update(updated)
        return getFacility(id)
    }

    @Transactional
    fun deleteFacility(id: UUID) {
        val existing = adminFacilityRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Facility not found")
        adminFacilityRepository.delete(existing.id)
    }

    @Transactional
    fun replaceContacts(facilityId: UUID, contacts: List<AdminContactPointCreate>) {
        requireFacility(facilityId)
        val rows = contacts.map {
            AdminContactPointRepository.ContactPointRow(
                id = UUID.randomUUID(),
                facilityId = facilityId,
                type = it.type,
                value = it.value,
                label = it.label,
                isPrimary = it.isPrimary
            )
        }
        adminContactPointRepository.replaceAllForFacility(facilityId, rows)
    }

    @Transactional
    fun replaceOpeningHours(facilityId: UUID, hours: List<AdminOpeningHours>) {
        requireFacility(facilityId)
        val rows = hours.map {
            AdminOpeningHoursRepository.OpeningHoursRow(
                facilityId = facilityId,
                dayOfWeek = it.dayOfWeek,
                isClosed = it.isClosed,
                openTime = parseLocalTime(it.openTime, "openTime"),
                closeTime = parseLocalTime(it.closeTime, "closeTime"),
                note = it.note
            )
        }
        adminOpeningHoursRepository.replaceAllForFacility(facilityId, rows)
    }

    @Transactional
    fun replaceCapabilities(facilityId: UUID, capabilities: List<AdminFacilityCapability>) {
        requireFacility(facilityId)
        val rows = capabilities.map {
            AdminFacilityCapabilityRepository.FacilityCapabilityRow(
                facilityId = facilityId,
                capabilityTypeId = it.capabilityTypeId,
                summary = it.summary,
                detailsJson = it.details?.let { json -> objectMapper.writeValueAsString(json) },
                isActive = it.isActive
            )
        }
        adminFacilityCapabilityRepository.replaceAllForFacility(facilityId, rows)
    }

    @Transactional
    fun replaceFeatures(facilityId: UUID, features: List<AdminFacilityFeature>) {
        requireFacility(facilityId)
        val rows = features.map {
            AdminFacilityFeatureRepository.FacilityFeatureRow(
                facilityId = facilityId,
                featureId = it.featureId,
                valueBool = it.valueBool,
                valueText = it.valueText,
                valueNumber = it.valueNumber
            )
        }
        adminFacilityFeatureRepository.replaceAllForFacility(facilityId, rows)
    }

    @Transactional
    fun replaceEquipment(facilityId: UUID, equipment: List<AdminFacilityEquipment>) {
        requireFacility(facilityId)
        val rows = equipment.map {
            AdminFacilityEquipmentRepository.FacilityEquipmentRow(
                facilityId = facilityId,
                equipmentTypeId = it.equipmentTypeId,
                quantity = it.quantity,
                mode = it.mode,
                note = it.note
            )
        }
        adminFacilityEquipmentRepository.replaceAllForFacility(facilityId, rows)
    }

    @Transactional
    fun replaceMedia(facilityId: UUID, media: List<AdminFacilityMedia>) {
        requireFacility(facilityId)
        val rows = media.map {
            AdminFacilityMediaRepository.FacilityMediaRow(
                facilityId = facilityId,
                mediaId = it.mediaId,
                sortOrder = it.sortOrder,
                caption = it.caption,
                isCover = it.isCover
            )
        }
        adminFacilityMediaRepository.replaceAllForFacility(facilityId, rows)
    }

    @Transactional
    fun replacePrices(facilityId: UUID, prices: List<AdminPriceInfoCreate>) {
        requireFacility(facilityId)
        val rows = prices.map {
            if (it.facilityId != facilityId) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Price info facilityId mismatch")
            }
            AdminPriceInfoRepository.PriceInfoRow(
                id = UUID.randomUUID(),
                facilityId = facilityId,
                capabilityTypeId = it.capabilityTypeId,
                spaceId = it.spaceId,
                kind = it.kind,
                amountFrom = it.amountFrom,
                amountTo = it.amountTo,
                currency = it.currency,
                note = it.note
            )
        }
        adminPriceInfoRepository.replaceAllForFacility(facilityId, rows)
    }

    private fun requireFacility(id: UUID) {
        if (adminFacilityRepository.fetchById(id) == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Facility not found")
        }
    }

    private fun validateAddressCity(cityId: UUID, addressId: UUID?) {
        if (addressId == null) return
        val address = adminAddressRepository.fetchById(addressId)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Address not found")
        if (address.cityId != cityId) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Address city must match facility city")
        }
    }

    private fun toAdminFacility(row: AdminFacilityRepository.FacilityRow): AdminFacility =
        AdminFacility(
            id = row.id,
            cityId = row.cityId,
            name = row.name,
            description = row.description,
            addressId = row.addressId,
            coverMediaId = row.coverMediaId,
            status = row.status,
            createdAt = row.createdAt
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Facility createdAt missing"),
            updatedAt = row.updatedAt
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Facility updatedAt missing")
        )

    private fun parseLocalTime(value: String?, fieldName: String): LocalTime? {
        if (value.isNullOrBlank()) return null
        val parsed = runCatching { LocalTime.parse(value) }.getOrNull()
        if (parsed == null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time for $fieldName")
        }
        return parsed
    }
}
