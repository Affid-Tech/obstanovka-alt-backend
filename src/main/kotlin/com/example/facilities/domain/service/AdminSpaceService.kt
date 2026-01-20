package com.example.facilities.domain.service

import com.example.facilities.api.admin.dto.AdminSpace
import com.example.facilities.api.admin.dto.AdminSpaceCreate
import com.example.facilities.api.admin.dto.AdminSpaceMedia
import com.example.facilities.api.admin.dto.AdminSpaceUpdate
import com.example.facilities.domain.repo.AdminSpaceMediaRepository
import com.example.facilities.domain.repo.AdminSpaceRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class AdminSpaceService(
    private val adminSpaceRepository: AdminSpaceRepository,
    private val adminSpaceMediaRepository: AdminSpaceMediaRepository
) {
    @Transactional(readOnly = true)
    fun getSpace(id: UUID): AdminSpace {
        val row = adminSpaceRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Space not found")
        return toAdminSpace(row)
    }

    @Transactional
    fun createSpace(request: AdminSpaceCreate): AdminSpace {
        val id = UUID.randomUUID()
        adminSpaceRepository.insert(
            AdminSpaceRepository.SpaceRow(
                id = id,
                facilityId = request.facilityId,
                spaceTypeId = request.spaceTypeId,
                name = request.name,
                description = request.description,
                capacityPeople = request.capacityPeople,
                sizeM2 = request.sizeM2
            )
        )
        return getSpace(id)
    }

    @Transactional
    fun updateSpace(id: UUID, request: AdminSpaceUpdate): AdminSpace {
        val existing = adminSpaceRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Space not found")
        val updated = AdminSpaceRepository.SpaceRow(
            id = existing.id,
            facilityId = existing.facilityId,
            spaceTypeId = request.spaceTypeId ?: existing.spaceTypeId,
            name = request.name ?: existing.name,
            description = request.description ?: existing.description,
            capacityPeople = request.capacityPeople ?: existing.capacityPeople,
            sizeM2 = request.sizeM2 ?: existing.sizeM2
        )
        adminSpaceRepository.update(updated)
        return getSpace(id)
    }

    @Transactional
    fun deleteSpace(id: UUID) {
        val existing = adminSpaceRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Space not found")
        adminSpaceRepository.delete(existing.id)
    }

    @Transactional
    fun replaceMedia(spaceId: UUID, media: List<AdminSpaceMedia>) {
        requireSpace(spaceId)
        val rows = media.map {
            AdminSpaceMediaRepository.SpaceMediaRow(
                spaceId = spaceId,
                mediaId = it.mediaId,
                sortOrder = it.sortOrder,
                caption = it.caption
            )
        }
        adminSpaceMediaRepository.replaceAllForSpace(spaceId, rows)
    }

    private fun requireSpace(id: UUID) {
        if (adminSpaceRepository.fetchById(id) == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Space not found")
        }
    }

    private fun toAdminSpace(row: AdminSpaceRepository.SpaceRow): AdminSpace =
        AdminSpace(
            id = row.id,
            facilityId = row.facilityId,
            spaceTypeId = row.spaceTypeId,
            name = row.name,
            description = row.description,
            capacityPeople = row.capacityPeople,
            sizeM2 = row.sizeM2
        )
}
