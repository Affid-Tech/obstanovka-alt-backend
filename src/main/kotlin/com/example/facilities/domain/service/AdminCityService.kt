package com.example.facilities.domain.service

import com.example.facilities.api.admin.dto.AdminCity
import com.example.facilities.api.admin.dto.AdminCityCreate
import com.example.facilities.api.admin.dto.AdminCityUpdate
import com.example.facilities.api.admin.dto.AdminCoordinates
import com.example.facilities.domain.repo.AdminCityRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class AdminCityService(
    private val adminCityRepository: AdminCityRepository
) {
    @Transactional(readOnly = true)
    fun listCities(): List<AdminCity> = adminCityRepository.fetchAll().map { row ->
        AdminCity(
            id = row.id,
            name = row.name,
            countryCode = row.countryCode,
            center = if (row.centerLat != null && row.centerLng != null) {
                AdminCoordinates(row.centerLat, row.centerLng)
            } else {
                null
            }
        )
    }

    @Transactional(readOnly = true)
    fun getCity(id: UUID): AdminCity =
        adminCityRepository.fetchById(id)?.let { row ->
            AdminCity(
                id = row.id,
                name = row.name,
                countryCode = row.countryCode,
                center = if (row.centerLat != null && row.centerLng != null) {
                    AdminCoordinates(row.centerLat, row.centerLng)
                } else {
                    null
                }
            )
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "City not found")

    @Transactional
    fun createCity(request: AdminCityCreate): AdminCity {
        val id = UUID.randomUUID()
        adminCityRepository.insert(
            AdminCityRepository.CityRow(
                id = id,
                name = request.name,
                countryCode = request.countryCode,
                centerLat = request.center?.lat,
                centerLng = request.center?.lng
            )
        )
        return getCity(id)
    }

    @Transactional
    fun updateCity(id: UUID, request: AdminCityUpdate): AdminCity {
        val existing = adminCityRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "City not found")
        val updated = AdminCityRepository.CityRow(
            id = existing.id,
            name = request.name ?: existing.name,
            countryCode = request.countryCode ?: existing.countryCode,
            centerLat = request.center?.lat ?: existing.centerLat,
            centerLng = request.center?.lng ?: existing.centerLng
        )
        adminCityRepository.update(updated)
        return getCity(id)
    }

    @Transactional
    fun deleteCity(id: UUID) {
        val existing = adminCityRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "City not found")
        adminCityRepository.delete(existing.id)
    }
}
