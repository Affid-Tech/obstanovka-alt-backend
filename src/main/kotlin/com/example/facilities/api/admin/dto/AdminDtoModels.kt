package com.example.facilities.api.admin.dto

import com.fasterxml.jackson.databind.JsonNode
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

data class AdminCoordinates(
    val lat: BigDecimal,
    val lng: BigDecimal
)

data class AdminCity(
    val id: UUID,
    val name: String,
    val countryCode: String,
    val center: AdminCoordinates? = null
)

data class AdminCityCreate(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    @field:Size(min = 2, max = 2)
    val countryCode: String,
    val center: AdminCoordinates? = null
)

data class AdminCityUpdate(
    @field:Size(min = 1, max = 255)
    val name: String? = null,
    @field:Size(min = 2, max = 2)
    val countryCode: String? = null,
    val center: AdminCoordinates? = null
)

data class AdminAddress(
    val id: UUID,
    val cityId: UUID,
    val label: String,
    val lat: BigDecimal? = null,
    val lng: BigDecimal? = null
)

data class AdminAddressCreate(
    @field:NotNull
    val cityId: UUID,
    @field:NotBlank
    val label: String,
    val lat: BigDecimal? = null,
    val lng: BigDecimal? = null
)

data class AdminAddressUpdate(
    val cityId: UUID? = null,
    @field:Size(min = 1, max = 255)
    val label: String? = null,
    val lat: BigDecimal? = null,
    val lng: BigDecimal? = null
)

data class AdminMedia(
    val id: UUID,
    val url: String,
    val kind: String,
    val createdAt: OffsetDateTime
)

data class AdminMediaCreate(
    @field:NotBlank
    @field:Pattern(regexp = "https?://.+", message = "url must be http(s)")
    val url: String,
    val kind: String? = null
)

data class AdminMediaUpdate(
    @field:Pattern(regexp = "https?://.+", message = "url must be http(s)")
    val url: String? = null,
    @field:Size(min = 1, max = 32)
    val kind: String? = null
)

data class AdminFacility(
    val id: UUID,
    val cityId: UUID,
    val name: String,
    val description: String? = null,
    val addressId: UUID? = null,
    val coverMediaId: UUID? = null,
    val status: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class AdminFacilityCreate(
    @field:NotNull
    val cityId: UUID,
    @field:NotBlank
    val name: String,
    val description: String? = null,
    val addressId: UUID? = null,
    val coverMediaId: UUID? = null,
    @field:NotBlank
    val status: String
)

data class AdminFacilityUpdate(
    val cityId: UUID? = null,
    @field:Size(min = 1, max = 255)
    val name: String? = null,
    val description: String? = null,
    val addressId: UUID? = null,
    val coverMediaId: UUID? = null,
    @field:Size(min = 1, max = 32)
    val status: String? = null
)

data class AdminFacilityMedia(
    @field:NotNull
    val mediaId: UUID,
    val sortOrder: Int = 0,
    val caption: String? = null,
    val isCover: Boolean = false
)

data class AdminContactPoint(
    val id: UUID,
    val facilityId: UUID,
    val type: String,
    val value: String,
    val label: String? = null,
    val isPrimary: Boolean
)

data class AdminContactPointCreate(
    @field:NotBlank
    val type: String,
    @field:NotBlank
    val value: String,
    val label: String? = null,
    val isPrimary: Boolean = false
)

data class AdminOpeningHours(
    @field:Min(1)
    @field:Max(7)
    val dayOfWeek: Int,
    val isClosed: Boolean,
    val openTime: String? = null,
    val closeTime: String? = null,
    val note: String? = null
)

data class AdminCapabilityType(
    val id: UUID,
    val code: String,
    val label: String
)

data class AdminCapabilityTypeCreate(
    @field:NotBlank
    val code: String,
    @field:NotBlank
    val label: String
)

data class AdminCapabilityTypeUpdate(
    @field:Size(min = 1, max = 64)
    val code: String? = null,
    @field:Size(min = 1, max = 255)
    val label: String? = null
)

data class AdminFacilityCapability(
    @field:NotNull
    val capabilityTypeId: UUID,
    val summary: String? = null,
    val details: JsonNode? = null,
    val isActive: Boolean = true
)

data class AdminSpaceType(
    val id: UUID,
    val code: String,
    val label: String
)

data class AdminSpaceTypeCreate(
    @field:NotBlank
    val code: String,
    @field:NotBlank
    val label: String
)

data class AdminSpaceTypeUpdate(
    @field:Size(min = 1, max = 64)
    val code: String? = null,
    @field:Size(min = 1, max = 255)
    val label: String? = null
)

data class AdminSpace(
    val id: UUID,
    val facilityId: UUID,
    val spaceTypeId: UUID,
    val name: String,
    val description: String? = null,
    val capacityPeople: Int? = null,
    val sizeM2: BigDecimal? = null
)

data class AdminSpaceCreate(
    @field:NotNull
    val facilityId: UUID,
    @field:NotNull
    val spaceTypeId: UUID,
    @field:NotBlank
    val name: String,
    val description: String? = null,
    val capacityPeople: Int? = null,
    val sizeM2: BigDecimal? = null
)

data class AdminSpaceUpdate(
    val spaceTypeId: UUID? = null,
    @field:Size(min = 1, max = 255)
    val name: String? = null,
    val description: String? = null,
    val capacityPeople: Int? = null,
    val sizeM2: BigDecimal? = null
)

data class AdminSpaceMedia(
    @field:NotNull
    val mediaId: UUID,
    val sortOrder: Int = 0,
    val caption: String? = null
)

data class AdminEquipmentType(
    val id: UUID,
    val name: String,
    val categoryCode: String,
    val description: String? = null,
    val coverMediaId: UUID? = null
)

data class AdminEquipmentTypeCreate(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val categoryCode: String,
    val description: String? = null,
    val coverMediaId: UUID? = null
)

data class AdminEquipmentTypeUpdate(
    @field:Size(min = 1, max = 255)
    val name: String? = null,
    @field:Size(min = 1, max = 64)
    val categoryCode: String? = null,
    val description: String? = null,
    val coverMediaId: UUID? = null
)

data class AdminFacilityEquipment(
    @field:NotNull
    val equipmentTypeId: UUID,
    val quantity: Int? = null,
    @field:NotBlank
    val mode: String,
    val note: String? = null
)

data class AdminFeature(
    val id: UUID,
    val code: String,
    val label: String,
    val valueType: String
)

data class AdminFeatureCreate(
    @field:NotBlank
    val code: String,
    @field:NotBlank
    val label: String,
    @field:NotBlank
    val valueType: String
)

data class AdminFeatureUpdate(
    @field:Size(min = 1, max = 64)
    val code: String? = null,
    @field:Size(min = 1, max = 255)
    val label: String? = null,
    @field:Size(min = 1, max = 32)
    val valueType: String? = null
)

data class AdminFacilityFeature(
    @field:NotNull
    val featureId: UUID,
    val valueBool: Boolean? = null,
    val valueText: String? = null,
    val valueNumber: BigDecimal? = null
)

data class AdminPriceInfo(
    val id: UUID,
    val facilityId: UUID,
    val capabilityTypeId: UUID? = null,
    val spaceId: UUID? = null,
    val kind: String,
    val amountFrom: BigDecimal? = null,
    val amountTo: BigDecimal? = null,
    val currency: String,
    val note: String? = null
)

data class AdminPriceInfoCreate(
    @field:NotNull
    val facilityId: UUID,
    val capabilityTypeId: UUID? = null,
    val spaceId: UUID? = null,
    @field:NotBlank
    val kind: String,
    val amountFrom: BigDecimal? = null,
    val amountTo: BigDecimal? = null,
    @field:NotBlank
    @field:Size(min = 3, max = 3)
    val currency: String,
    val note: String? = null
)

data class AdminPriceInfoUpdate(
    val capabilityTypeId: UUID? = null,
    val spaceId: UUID? = null,
    @field:Size(min = 1, max = 32)
    val kind: String? = null,
    val amountFrom: BigDecimal? = null,
    val amountTo: BigDecimal? = null,
    @field:Size(min = 3, max = 3)
    val currency: String? = null,
    val note: String? = null
)
