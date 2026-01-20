package com.example.facilities.api.admin.controller

import com.example.facilities.api.admin.dto.AdminMedia
import com.example.facilities.api.admin.dto.AdminMediaCreate
import com.example.facilities.api.admin.dto.AdminMediaUpdate
import com.example.facilities.domain.service.AdminMediaService
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
@RequestMapping("/admin/v1/media")
class AdminMediaController(
    private val adminMediaService: AdminMediaService
) {
    @GetMapping("/{id}")
    fun getMedia(@PathVariable id: UUID): AdminMedia = adminMediaService.getMedia(id)

    @PostMapping
    fun createMedia(@Valid @RequestBody request: AdminMediaCreate): AdminMedia =
        adminMediaService.createMedia(request)

    @PutMapping("/{id}")
    fun updateMedia(
        @PathVariable id: UUID,
        @Valid @RequestBody request: AdminMediaUpdate
    ): AdminMedia = adminMediaService.updateMedia(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMedia(@PathVariable id: UUID) {
        adminMediaService.deleteMedia(id)
    }
}
