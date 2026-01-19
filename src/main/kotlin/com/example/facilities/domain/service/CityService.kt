package com.example.facilities.domain.service

import com.example.facilities.api.dto.CityDTO
import com.example.facilities.api.dto.CoordinatesDTO
import com.example.facilities.domain.repo.CityRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CityService(
    private val cityRepository: CityRepository
) {
    @Transactional(readOnly = true)
    fun listCities(): List<CityDTO> = cityRepository.fetchCities().map { row ->
        CityDTO(
            id = row.id,
            name = row.name,
            countryCode = row.countryCode,
            center = if (row.centerLat != null && row.centerLng != null) {
                CoordinatesDTO(row.centerLat, row.centerLng)
            } else {
                null
            }
        )
    }
}
