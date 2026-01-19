package com.example.facilities.api.controller

import com.example.facilities.api.dto.FacilityDetailsResponse
import com.example.facilities.api.dto.FacilityListResponse
import com.example.facilities.domain.service.FacilityService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.util.UUID

@RestController
@RequestMapping("/v1/facilities")
class FacilityController(
    private val facilityService: FacilityService
) {
    @GetMapping
    fun listFacilities(
        @RequestParam cityId: UUID,
        @RequestParam(required = false) q: String?,
        @RequestParam(required = false) capability: List<String>?,
        @RequestParam(required = false) feature: List<String>?,
        @RequestParam(required = false) equipmentCategory: List<String>?,
        @RequestParam(required = false) spaceType: List<String>?,
        @RequestParam(required = false) hasAddress: Boolean?,
        @RequestParam(required = false) hasCoordinates: Boolean?,
        @RequestParam(required = false) priceMin: BigDecimal?,
        @RequestParam(required = false) priceMax: BigDecimal?,
        @RequestParam(required = false, defaultValue = "RECOMMENDED") sort: String,
        @RequestParam(required = false, defaultValue = "1") page: Int,
        @RequestParam(required = false, defaultValue = "20") pageSize: Int
    ): FacilityListResponse {
        val normalizedPage = page.coerceAtLeast(1)
        val normalizedPageSize = pageSize.coerceIn(1, 50)

        val (items, total) = facilityService.listFacilities(
            cityId = cityId,
            query = q,
            capability = capability,
            feature = feature,
            equipmentCategory = equipmentCategory,
            spaceType = spaceType,
            hasAddress = hasAddress,
            hasCoordinates = hasCoordinates,
            priceMin = priceMin,
            priceMax = priceMax,
            sort = sort.trim().uppercase(),
            page = normalizedPage,
            pageSize = normalizedPageSize
        )

        return FacilityListResponse(
            items = items,
            page = normalizedPage,
            pageSize = normalizedPageSize,
            total = total
        )
    }

    @GetMapping("/{facilityId}")
    fun getFacilityDetails(@PathVariable facilityId: UUID): FacilityDetailsResponse {
        val item = facilityService.getFacilityDetails(facilityId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Facility not found")
        return FacilityDetailsResponse(item)
    }
}
