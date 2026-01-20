package com.example.facilities.domain.service

import com.example.facilities.api.admin.dto.AdminAddress
import com.example.facilities.api.admin.dto.AdminAddressCreate
import com.example.facilities.api.admin.dto.AdminAddressUpdate
import com.example.facilities.domain.repo.AdminAddressRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class AdminAddressService(
    private val adminAddressRepository: AdminAddressRepository
) {
    @Transactional(readOnly = true)
    fun getAddress(id: UUID): AdminAddress =
        adminAddressRepository.fetchById(id)?.let { row ->
            AdminAddress(
                id = row.id,
                cityId = row.cityId,
                label = row.label,
                lat = row.lat,
                lng = row.lng
            )
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found")

    @Transactional
    fun createAddress(request: AdminAddressCreate): AdminAddress {
        val id = UUID.randomUUID()
        adminAddressRepository.insert(
            AdminAddressRepository.AddressRow(
                id = id,
                cityId = request.cityId,
                label = request.label,
                lat = request.lat,
                lng = request.lng
            )
        )
        return getAddress(id)
    }

    @Transactional
    fun updateAddress(id: UUID, request: AdminAddressUpdate): AdminAddress {
        val existing = adminAddressRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found")
        val updated = AdminAddressRepository.AddressRow(
            id = existing.id,
            cityId = request.cityId ?: existing.cityId,
            label = request.label ?: existing.label,
            lat = request.lat ?: existing.lat,
            lng = request.lng ?: existing.lng
        )
        adminAddressRepository.update(updated)
        return getAddress(id)
    }

    @Transactional
    fun deleteAddress(id: UUID) {
        val existing = adminAddressRepository.fetchById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found")
        adminAddressRepository.delete(existing.id)
    }
}
