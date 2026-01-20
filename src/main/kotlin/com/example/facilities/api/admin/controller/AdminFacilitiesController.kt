package com.example.facilities.api.admin.controller

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
import com.example.facilities.domain.service.AdminFacilityService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/admin/v1/facilities")
class AdminFacilitiesController(
    private val adminFacilityService: AdminFacilityService
) {
    @GetMapping("/{id}")
    fun getFacility(@PathVariable id: UUID): AdminFacility = adminFacilityService.getFacility(id)

    @PostMapping
    fun createFacility(@Valid @RequestBody request: AdminFacilityCreate): AdminFacility =
        adminFacilityService.createFacility(request)

    @PutMapping("/{id}")
    fun updateFacility(
        @PathVariable id: UUID,
        @Valid @RequestBody request: AdminFacilityUpdate
    ): AdminFacility = adminFacilityService.updateFacility(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteFacility(@PathVariable id: UUID) {
        adminFacilityService.deleteFacility(id)
    }

    @PutMapping("/{id}/contacts")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun replaceContacts(
        @PathVariable id: UUID,
        @Valid @RequestBody contacts: List<AdminContactPointCreate>
    ) {
        adminFacilityService.replaceContacts(id, contacts)
    }

    @PutMapping("/{id}/opening-hours")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun replaceOpeningHours(
        @PathVariable id: UUID,
        @Valid @RequestBody hours: List<AdminOpeningHours>
    ) {
        adminFacilityService.replaceOpeningHours(id, hours)
    }

    @PutMapping("/{id}/capabilities")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun replaceCapabilities(
        @PathVariable id: UUID,
        @Valid @RequestBody capabilities: List<AdminFacilityCapability>
    ) {
        adminFacilityService.replaceCapabilities(id, capabilities)
    }

    @PutMapping("/{id}/features")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun replaceFeatures(
        @PathVariable id: UUID,
        @Valid @RequestBody features: List<AdminFacilityFeature>
    ) {
        adminFacilityService.replaceFeatures(id, features)
    }

    @PutMapping("/{id}/equipment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun replaceEquipment(
        @PathVariable id: UUID,
        @Valid @RequestBody equipment: List<AdminFacilityEquipment>
    ) {
        adminFacilityService.replaceEquipment(id, equipment)
    }

    @PutMapping("/{id}/prices")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun replacePrices(
        @PathVariable id: UUID,
        @Valid @RequestBody prices: List<AdminPriceInfoCreate>
    ) {
        adminFacilityService.replacePrices(id, prices)
    }

    @PutMapping("/{id}/media")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun replaceMedia(
        @PathVariable id: UUID,
        @Valid @RequestBody media: List<AdminFacilityMedia>
    ) {
        adminFacilityService.replaceMedia(id, media)
    }
}
