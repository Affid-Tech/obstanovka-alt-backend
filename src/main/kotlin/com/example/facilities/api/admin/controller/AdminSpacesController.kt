package com.example.facilities.api.admin.controller

import com.example.facilities.api.admin.dto.AdminSpace
import com.example.facilities.api.admin.dto.AdminSpaceCreate
import com.example.facilities.api.admin.dto.AdminSpaceMedia
import com.example.facilities.api.admin.dto.AdminSpaceUpdate
import com.example.facilities.domain.service.AdminSpaceService
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
@RequestMapping("/admin/v1/spaces")
class AdminSpacesController(
    private val adminSpaceService: AdminSpaceService
) {
    @GetMapping("/{id}")
    fun getSpace(@PathVariable id: UUID): AdminSpace = adminSpaceService.getSpace(id)

    @PostMapping
    fun createSpace(@Valid @RequestBody request: AdminSpaceCreate): AdminSpace =
        adminSpaceService.createSpace(request)

    @PutMapping("/{id}")
    fun updateSpace(
        @PathVariable id: UUID,
        @Valid @RequestBody request: AdminSpaceUpdate
    ): AdminSpace = adminSpaceService.updateSpace(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSpace(@PathVariable id: UUID) {
        adminSpaceService.deleteSpace(id)
    }

    @PutMapping("/{id}/media")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun replaceMedia(
        @PathVariable id: UUID,
        @Valid @RequestBody media: List<AdminSpaceMedia>
    ) {
        adminSpaceService.replaceMedia(id, media)
    }
}
