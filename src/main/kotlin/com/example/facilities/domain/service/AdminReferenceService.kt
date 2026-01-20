package com.example.facilities.domain.service

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
import com.example.facilities.domain.repo.AdminCapabilityTypeRepository
import com.example.facilities.domain.repo.AdminEquipmentTypeRepository
import com.example.facilities.domain.repo.AdminFeatureRepository
import com.example.facilities.domain.repo.AdminSpaceTypeRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class AdminReferenceService(
    private val adminCapabilityTypeRepository: AdminCapabilityTypeRepository,
    private val adminFeatureRepository: AdminFeatureRepository,
    private val adminEquipmentTypeRepository: AdminEquipmentTypeRepository,
    private val adminSpaceTypeRepository: AdminSpaceTypeRepository
) {
    @Transactional(readOnly = true)
    fun listCapabilityTypes(): List<AdminCapabilityType> =
        adminCapabilityTypeRepository.fetchAll().map { row ->
            AdminCapabilityType(row.id, row.code, row.label)
        }

    @Transactional(readOnly = true)
    fun getCapabilityType(id: UUID): AdminCapabilityType =
        adminCapabilityTypeRepository.fetchById(id)?.let { row ->
            AdminCapabilityType(row.id, row.code, row.label)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Capability type not found")

    @Transactional
    fun createCapabilityType(request: AdminCapabilityTypeCreate): AdminCapabilityType {
        val id = UUID.randomUUID()
        adminCapabilityTypeRepository.insert(
            AdminCapabilityTypeRepository.CapabilityTypeRow(
                id = id,
                code = request.code,
                label = request.label
            )
        )
        return getCapabilityType(id)
    }

    @Transactional
    fun updateCapabilityType(id: UUID, request: AdminCapabilityTypeUpdate): AdminCapabilityType {
        val existing = adminCapabilityTypeRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Capability type not found")
        val updated = AdminCapabilityTypeRepository.CapabilityTypeRow(
            id = existing.id,
            code = request.code ?: existing.code,
            label = request.label ?: existing.label
        )
        adminCapabilityTypeRepository.update(updated)
        return getCapabilityType(id)
    }

    @Transactional
    fun deleteCapabilityType(id: UUID) {
        val existing = adminCapabilityTypeRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Capability type not found")
        if (adminCapabilityTypeRepository.isReferenced(id)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Capability type is referenced")
        }
        adminCapabilityTypeRepository.delete(existing.id)
    }

    @Transactional(readOnly = true)
    fun listFeatures(): List<AdminFeature> =
        adminFeatureRepository.fetchAll().map { row ->
            AdminFeature(row.id, row.code, row.label, row.valueType)
        }

    @Transactional(readOnly = true)
    fun getFeature(id: UUID): AdminFeature =
        adminFeatureRepository.fetchById(id)?.let { row ->
            AdminFeature(row.id, row.code, row.label, row.valueType)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Feature not found")

    @Transactional
    fun createFeature(request: AdminFeatureCreate): AdminFeature {
        val id = UUID.randomUUID()
        adminFeatureRepository.insert(
            AdminFeatureRepository.FeatureRow(
                id = id,
                code = request.code,
                label = request.label,
                valueType = request.valueType
            )
        )
        return getFeature(id)
    }

    @Transactional
    fun updateFeature(id: UUID, request: AdminFeatureUpdate): AdminFeature {
        val existing = adminFeatureRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Feature not found")
        val updated = AdminFeatureRepository.FeatureRow(
            id = existing.id,
            code = request.code ?: existing.code,
            label = request.label ?: existing.label,
            valueType = request.valueType ?: existing.valueType
        )
        adminFeatureRepository.update(updated)
        return getFeature(id)
    }

    @Transactional
    fun deleteFeature(id: UUID) {
        val existing = adminFeatureRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Feature not found")
        if (adminFeatureRepository.isReferenced(id)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Feature is referenced")
        }
        adminFeatureRepository.delete(existing.id)
    }

    @Transactional(readOnly = true)
    fun listEquipmentTypes(): List<AdminEquipmentType> =
        adminEquipmentTypeRepository.fetchAll().map { row ->
            AdminEquipmentType(
                id = row.id,
                name = row.name,
                categoryCode = row.categoryCode,
                description = row.description,
                coverMediaId = row.coverMediaId
            )
        }

    @Transactional(readOnly = true)
    fun getEquipmentType(id: UUID): AdminEquipmentType =
        adminEquipmentTypeRepository.fetchById(id)?.let { row ->
            AdminEquipmentType(
                id = row.id,
                name = row.name,
                categoryCode = row.categoryCode,
                description = row.description,
                coverMediaId = row.coverMediaId
            )
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Equipment type not found")

    @Transactional
    fun createEquipmentType(request: AdminEquipmentTypeCreate): AdminEquipmentType {
        val id = UUID.randomUUID()
        adminEquipmentTypeRepository.insert(
            AdminEquipmentTypeRepository.EquipmentTypeRow(
                id = id,
                name = request.name,
                categoryCode = request.categoryCode,
                description = request.description,
                coverMediaId = request.coverMediaId
            )
        )
        return getEquipmentType(id)
    }

    @Transactional
    fun updateEquipmentType(id: UUID, request: AdminEquipmentTypeUpdate): AdminEquipmentType {
        val existing = adminEquipmentTypeRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Equipment type not found")
        val updated = AdminEquipmentTypeRepository.EquipmentTypeRow(
            id = existing.id,
            name = request.name ?: existing.name,
            categoryCode = request.categoryCode ?: existing.categoryCode,
            description = request.description ?: existing.description,
            coverMediaId = request.coverMediaId ?: existing.coverMediaId
        )
        adminEquipmentTypeRepository.update(updated)
        return getEquipmentType(id)
    }

    @Transactional
    fun deleteEquipmentType(id: UUID) {
        val existing = adminEquipmentTypeRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Equipment type not found")
        if (adminEquipmentTypeRepository.isReferenced(id)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Equipment type is referenced")
        }
        adminEquipmentTypeRepository.delete(existing.id)
    }

    @Transactional(readOnly = true)
    fun listSpaceTypes(): List<AdminSpaceType> =
        adminSpaceTypeRepository.fetchAll().map { row ->
            AdminSpaceType(row.id, row.code, row.label)
        }

    @Transactional(readOnly = true)
    fun getSpaceType(id: UUID): AdminSpaceType =
        adminSpaceTypeRepository.fetchById(id)?.let { row ->
            AdminSpaceType(row.id, row.code, row.label)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Space type not found")

    @Transactional
    fun createSpaceType(request: AdminSpaceTypeCreate): AdminSpaceType {
        val id = UUID.randomUUID()
        adminSpaceTypeRepository.insert(
            AdminSpaceTypeRepository.SpaceTypeRow(
                id = id,
                code = request.code,
                label = request.label
            )
        )
        return getSpaceType(id)
    }

    @Transactional
    fun updateSpaceType(id: UUID, request: AdminSpaceTypeUpdate): AdminSpaceType {
        val existing = adminSpaceTypeRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Space type not found")
        val updated = AdminSpaceTypeRepository.SpaceTypeRow(
            id = existing.id,
            code = request.code ?: existing.code,
            label = request.label ?: existing.label
        )
        adminSpaceTypeRepository.update(updated)
        return getSpaceType(id)
    }

    @Transactional
    fun deleteSpaceType(id: UUID) {
        val existing = adminSpaceTypeRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Space type not found")
        if (adminSpaceTypeRepository.isReferenced(id)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Space type is referenced")
        }
        adminSpaceTypeRepository.delete(existing.id)
    }
}
