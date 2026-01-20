package com.example.facilities.domain.service

import com.example.facilities.api.admin.dto.AdminFacilityCreate
import com.example.facilities.domain.repo.AdminAddressRepository
import com.example.facilities.domain.repo.AdminContactPointRepository
import com.example.facilities.domain.repo.AdminFacilityCapabilityRepository
import com.example.facilities.domain.repo.AdminFacilityEquipmentRepository
import com.example.facilities.domain.repo.AdminFacilityFeatureRepository
import com.example.facilities.domain.repo.AdminFacilityMediaRepository
import com.example.facilities.domain.repo.AdminFacilityRepository
import com.example.facilities.domain.repo.AdminOpeningHoursRepository
import com.example.facilities.domain.repo.AdminPriceInfoRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

class AdminFacilityServiceTest {
    private val adminFacilityRepository = mock(AdminFacilityRepository::class.java)
    private val adminAddressRepository = mock(AdminAddressRepository::class.java)
    private val adminContactPointRepository = mock(AdminContactPointRepository::class.java)
    private val adminOpeningHoursRepository = mock(AdminOpeningHoursRepository::class.java)
    private val adminFacilityCapabilityRepository = mock(AdminFacilityCapabilityRepository::class.java)
    private val adminFacilityFeatureRepository = mock(AdminFacilityFeatureRepository::class.java)
    private val adminFacilityEquipmentRepository = mock(AdminFacilityEquipmentRepository::class.java)
    private val adminFacilityMediaRepository = mock(AdminFacilityMediaRepository::class.java)
    private val adminPriceInfoRepository = mock(AdminPriceInfoRepository::class.java)
    private val objectMapper = ObjectMapper()

    private val service = AdminFacilityService(
        adminFacilityRepository,
        adminAddressRepository,
        adminContactPointRepository,
        adminOpeningHoursRepository,
        adminFacilityCapabilityRepository,
        adminFacilityFeatureRepository,
        adminFacilityEquipmentRepository,
        adminFacilityMediaRepository,
        adminPriceInfoRepository,
        objectMapper
    )

    @Test
    fun `createFacility rejects address from a different city`() {
        val cityId = UUID.randomUUID()
        val addressId = UUID.randomUUID()
        val otherCityId = UUID.randomUUID()

        `when`(adminAddressRepository.fetchById(addressId)).thenReturn(
            AdminAddressRepository.AddressRow(
                id = addressId,
                cityId = otherCityId,
                label = "Mismatch",
                lat = null,
                lng = null
            )
        )

        val exception = assertThrows(ResponseStatusException::class.java) {
            service.createFacility(
                AdminFacilityCreate(
                    cityId = cityId,
                    name = "Test Facility",
                    description = null,
                    addressId = addressId,
                    coverMediaId = null,
                    status = "ACTIVE"
                )
            )
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
        assertEquals("Address city must match facility city", exception.reason)
    }
}
