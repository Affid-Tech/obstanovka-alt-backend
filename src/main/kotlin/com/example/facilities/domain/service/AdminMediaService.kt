package com.example.facilities.domain.service

import com.example.facilities.api.admin.dto.AdminMedia
import com.example.facilities.api.admin.dto.AdminMediaCreate
import com.example.facilities.api.admin.dto.AdminMediaUpdate
import com.example.facilities.domain.repo.AdminMediaRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import java.util.UUID

@Service
class AdminMediaService(
    private val adminMediaRepository: AdminMediaRepository
) {
    @Transactional(readOnly = true)
    fun getMedia(id: UUID): AdminMedia =
        adminMediaRepository.fetchById(id)?.let { row ->
            AdminMedia(
                id = row.id,
                url = row.url,
                kind = row.kind,
                createdAt = row.createdAt
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Media createdAt missing")
            )
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Media not found")

    @Transactional
    fun createMedia(request: AdminMediaCreate): AdminMedia {
        ensureHttpUrl(request.url)
        val id = UUID.randomUUID()
        adminMediaRepository.insert(
            AdminMediaRepository.MediaRow(
                id = id,
                url = request.url,
                kind = request.kind ?: "IMAGE",
                createdAt = null
            )
        )
        return getMedia(id)
    }

    @Transactional
    fun updateMedia(id: UUID, request: AdminMediaUpdate): AdminMedia {
        val existing = adminMediaRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Media not found")
        request.url?.let { ensureHttpUrl(it) }
        val updated = AdminMediaRepository.MediaRow(
            id = existing.id,
            url = request.url ?: existing.url,
            kind = request.kind ?: existing.kind,
            createdAt = existing.createdAt
        )
        adminMediaRepository.update(updated)
        return getMedia(id)
    }

    @Transactional
    fun deleteMedia(id: UUID) {
        val existing = adminMediaRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Media not found")
        adminMediaRepository.delete(existing.id)
    }

    private fun ensureHttpUrl(url: String) {
        val uri = runCatching { URI(url) }.getOrNull()
        val scheme = uri?.scheme?.lowercase()
        if (scheme != "http" && scheme != "https") {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "media.url must be http(s)")
        }
    }
}
