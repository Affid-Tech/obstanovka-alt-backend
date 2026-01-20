package com.example.facilities.api.admin.controller

import com.example.facilities.api.admin.dto.AdminCapabilityType
import com.example.facilities.api.admin.dto.AdminCapabilityTypeCreate
import com.example.facilities.api.admin.dto.AdminCapabilityTypeUpdate
import com.example.facilities.api.admin.dto.AdminEquipmentType
import com.example.facilities.api.admin.dto.AdminEquipmentTypeCreate
import com.example.facilities.api.admin.dto.AdminEquipmentTypeUpdate
import com.example.facilities.api.admin.dto.AdminFeature
import com.example.facilities.api.admin.dto.AdminFeatureCreate
import com.example.facilities.api.admin.dto.AdminFeatureUpdate
import com.example.facilities.api.admin.dto.AdminSpaceType
import com.example.facilities.api.admin.dto.AdminSpaceTypeCreate
import com.example.facilities.api.admin.dto.AdminSpaceTypeUpdate
import com.example.facilities.domain.service.AdminReferenceService
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
@RequestMapping("/admin/v1/reference")
class AdminReferenceController(
    private val adminReferenceService: AdminReferenceService
) {
    @GetMapping("/capability-types")
    fun listCapabilityTypes(): List<AdminCapabilityType> = adminReferenceService.listCapabilityTypes()

    @GetMapping("/capability-types/{id}")
    fun getCapabilityType(@PathVariable id: UUID): AdminCapabilityType =
        adminReferenceService.getCapabilityType(id)

    @PostMapping("/capability-types")
    fun createCapabilityType(
        @Valid @RequestBody request: AdminCapabilityTypeCreate
    ): AdminCapabilityType = adminReferenceService.createCapabilityType(request)

    @PutMapping("/capability-types/{id}")
    fun updateCapabilityType(
        @PathVariable id: UUID,
        @Valid @RequestBody request: AdminCapabilityTypeUpdate
    ): AdminCapabilityType = adminReferenceService.updateCapabilityType(id, request)

    @DeleteMapping("/capability-types/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCapabilityType(@PathVariable id: UUID) {
        adminReferenceService.deleteCapabilityType(id)
    }

    @GetMapping("/features")
    fun listFeatures(): List<AdminFeature> = adminReferenceService.listFeatures()

    @GetMapping("/features/{id}")
    fun getFeature(@PathVariable id: UUID): AdminFeature = adminReferenceService.getFeature(id)

    @PostMapping("/features")
    fun createFeature(@Valid @RequestBody request: AdminFeatureCreate): AdminFeature =
        adminReferenceService.createFeature(request)

    @PutMapping("/features/{id}")
    fun updateFeature(
        @PathVariable id: UUID,
        @Valid @RequestBody request: AdminFeatureUpdate
    ): AdminFeature = adminReferenceService.updateFeature(id, request)

    @DeleteMapping("/features/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteFeature(@PathVariable id: UUID) {
        adminReferenceService.deleteFeature(id)
    }

    @GetMapping("/space-types")
    fun listSpaceTypes(): List<AdminSpaceType> = adminReferenceService.listSpaceTypes()

    @GetMapping("/space-types/{id}")
    fun getSpaceType(@PathVariable id: UUID): AdminSpaceType = adminReferenceService.getSpaceType(id)

    @PostMapping("/space-types")
    fun createSpaceType(@Valid @RequestBody request: AdminSpaceTypeCreate): AdminSpaceType =
        adminReferenceService.createSpaceType(request)

    @PutMapping("/space-types/{id}")
    fun updateSpaceType(
        @PathVariable id: UUID,
        @Valid @RequestBody request: AdminSpaceTypeUpdate
    ): AdminSpaceType = adminReferenceService.updateSpaceType(id, request)

    @DeleteMapping("/space-types/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSpaceType(@PathVariable id: UUID) {
        adminReferenceService.deleteSpaceType(id)
    }

    @GetMapping("/equipment-types")
    fun listEquipmentTypes(): List<AdminEquipmentType> = adminReferenceService.listEquipmentTypes()

    @GetMapping("/equipment-types/{id}")
    fun getEquipmentType(@PathVariable id: UUID): AdminEquipmentType =
        adminReferenceService.getEquipmentType(id)

    @PostMapping("/equipment-types")
    fun createEquipmentType(
        @Valid @RequestBody request: AdminEquipmentTypeCreate
    ): AdminEquipmentType = adminReferenceService.createEquipmentType(request)

    @PutMapping("/equipment-types/{id}")
    fun updateEquipmentType(
        @PathVariable id: UUID,
        @Valid @RequestBody request: AdminEquipmentTypeUpdate
    ): AdminEquipmentType = adminReferenceService.updateEquipmentType(id, request)

    @DeleteMapping("/equipment-types/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteEquipmentType(@PathVariable id: UUID) {
        adminReferenceService.deleteEquipmentType(id)
    }
}
