package com.example.facilities.api.admin.controller

import com.example.facilities.api.admin.dto.AdminCity
import com.example.facilities.api.admin.dto.AdminCityCreate
import com.example.facilities.api.admin.dto.AdminCityUpdate
import com.example.facilities.domain.service.AdminCityService
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
@RequestMapping("/admin/v1/cities")
class AdminCitiesController(
    private val adminCityService: AdminCityService
) {
    @GetMapping
    fun listCities(): List<AdminCity> = adminCityService.listCities()

    @GetMapping("/{id}")
    fun getCity(@PathVariable id: UUID): AdminCity = adminCityService.getCity(id)

    @PostMapping
    fun createCity(@Valid @RequestBody request: AdminCityCreate): AdminCity =
        adminCityService.createCity(request)

    @PutMapping("/{id}")
    fun updateCity(
        @PathVariable id: UUID,
        @Valid @RequestBody request: AdminCityUpdate
    ): AdminCity = adminCityService.updateCity(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCity(@PathVariable id: UUID) {
        adminCityService.deleteCity(id)
    }
}
