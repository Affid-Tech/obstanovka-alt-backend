package com.example.facilities.api.dto

import com.fasterxml.jackson.databind.JsonNode
import java.math.BigDecimal
import java.util.UUID

data class CityDTO(
    val id: UUID,
    val name: String,
    val countryCode: String,
    val center: CoordinatesDTO? = null
)

data class CoordinatesDTO(
    val lat: BigDecimal,
    val lng: BigDecimal
)

data class CityListResponse(
    val items: List<CityDTO>
)

data class MetaItemDTO(
    val code: String,
    val label: String
)

data class FeatureMetaDTO(
    val code: String,
    val label: String,
    val valueType: String
)

data class MetaResponse(
    val capabilities: List<MetaItemDTO>,
    val features: List<FeatureMetaDTO>,
    val equipmentCategories: List<MetaItemDTO>,
    val spaceTypes: List<MetaItemDTO>
)

data class CityRefDTO(
    val id: UUID,
    val name: String
)

data class PriceHintDTO(
    val kind: String,
    val amountFrom: BigDecimal? = null,
    val amountTo: BigDecimal? = null,
    val currency: String,
    val note: String? = null
)

data class FacilityCapabilityDTO(
    val code: String,
    val label: String
)

data class FacilityCardDTO(
    val id: UUID,
    val name: String,
    val city: CityRefDTO,
    val addressLabel: String? = null,
    val coordinates: CoordinatesDTO? = null,
    val coverImageUrl: String? = null,
    val capabilities: List<FacilityCapabilityDTO>,
    val priceHint: PriceHintDTO? = null,
    val featureCodes: List<String>,
    val equipmentCategoryCodes: List<String>
)

data class FacilityListResponse(
    val items: List<FacilityCardDTO>,
    val page: Int,
    val pageSize: Int,
    val total: Long
)

data class ContactPointDTO(
    val type: String,
    val value: String,
    val label: String? = null,
    val isPrimary: Boolean
)

data class OpeningHoursDTO(
    val dayOfWeek: Int,
    val isClosed: Boolean,
    val openTime: String? = null,
    val closeTime: String? = null,
    val note: String? = null
)

data class MediaItemDTO(
    val url: String,
    val caption: String? = null,
    val sortOrder: Int
)

data class SpaceAttributeDTO(
    val code: String,
    val label: String,
    val value: Any,
    val unit: String? = null
)

data class SpaceDTO(
    val id: UUID,
    val name: String,
    val typeCode: String,
    val capacityPeople: Int? = null,
    val sizeM2: BigDecimal? = null,
    val description: String? = null,
    val media: List<MediaItemDTO> = emptyList(),
    val attributes: List<SpaceAttributeDTO> = emptyList()
)

data class CapabilityDetailsDTO(
    val code: String,
    val label: String,
    val summary: String? = null,
    val details: JsonNode? = null
)

data class EquipmentItemDTO(
    val name: String,
    val quantity: Int? = null,
    val mode: String,
    val note: String? = null
)

data class EquipmentCategoryDTO(
    val category: String,
    val items: List<EquipmentItemDTO>
)

data class FeatureValueDTO(
    val code: String,
    val label: String,
    val value: Any? = null
)

data class FacilityDetailsDTO(
    val id: UUID,
    val name: String,
    val city: CityRefDTO,
    val addressLabel: String? = null,
    val coordinates: CoordinatesDTO? = null,
    val coverImageUrl: String? = null,
    val capabilities: List<FacilityCapabilityDTO>,
    val priceHint: PriceHintDTO? = null,
    val featureCodes: List<String>,
    val equipmentCategoryCodes: List<String>,
    val description: String? = null,
    val gallery: List<MediaItemDTO>,
    val contacts: List<ContactPointDTO>,
    val openingHours: List<OpeningHoursDTO>,
    val capabilityDetails: List<CapabilityDetailsDTO>,
    val spaces: List<SpaceDTO> = emptyList(),
    val equipment: List<EquipmentCategoryDTO>,
    val features: List<FeatureValueDTO>
)

data class FacilityDetailsResponse(
    val item: FacilityDetailsDTO
)
