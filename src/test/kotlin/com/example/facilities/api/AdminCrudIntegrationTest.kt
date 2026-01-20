package com.example.facilities.api

import com.example.facilities.support.BaseIntegrationTest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

class AdminCrudIntegrationTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `admin city CRUD works`() {
        val createPayload = mapOf(
            "name" to "Testville",
            "countryCode" to "US"
        )

        val createResponse = mockMvc.perform(
            post("/admin/v1/cities")
                .with(httpBasic("admin", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPayload))
        )
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        val cityId = objectMapper.readTree(createResponse).get("id").asText()

        mockMvc.perform(
            get("/admin/v1/cities/$cityId")
                .with(httpBasic("admin", "password"))
        )
            .andExpect(status().isOk)

        val updatePayload = mapOf(
            "name" to "Updatedville"
        )

        val updateResponse = mockMvc.perform(
            put("/admin/v1/cities/$cityId")
                .with(httpBasic("admin", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePayload))
        )
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assertEquals("Updatedville", objectMapper.readTree(updateResponse).get("name").asText())

        mockMvc.perform(
            delete("/admin/v1/cities/$cityId")
                .with(httpBasic("admin", "password"))
        )
            .andExpect(status().isNoContent)

        mockMvc.perform(
            get("/admin/v1/cities/$cityId")
                .with(httpBasic("admin", "password"))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `capability type delete conflicts when referenced`() {
        val capabilityPayload = mapOf(
            "code" to "TEST_CAP_${UUID.randomUUID()}",
            "label" to "Test Capability"
        )

        val capabilityResponse = mockMvc.perform(
            post("/admin/v1/reference/capability-types")
                .with(httpBasic("admin", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(capabilityPayload))
        )
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        val capabilityId = objectMapper.readTree(capabilityResponse).get("id").asText()

        val cityResponse = mockMvc.perform(
            post("/admin/v1/cities")
                .with(httpBasic("admin", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        mapOf(
                            "name" to "Capability City",
                            "countryCode" to "CA"
                        )
                    )
                )
        )
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        val cityId = objectMapper.readTree(cityResponse).get("id").asText()

        val facilityResponse = mockMvc.perform(
            post("/admin/v1/facilities")
                .with(httpBasic("admin", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        mapOf(
                            "cityId" to cityId,
                            "name" to "Capability Facility",
                            "status" to "ACTIVE"
                        )
                    )
                )
        )
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        val facilityId = objectMapper.readTree(facilityResponse).get("id").asText()

        val capabilityListPayload = listOf(
            mapOf(
                "capabilityTypeId" to capabilityId,
                "summary" to "Attached"
            )
        )

        mockMvc.perform(
            put("/admin/v1/facilities/$facilityId/capabilities")
                .with(httpBasic("admin", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(capabilityListPayload))
        )
            .andExpect(status().isNoContent)

        mockMvc.perform(
            delete("/admin/v1/reference/capability-types/$capabilityId")
                .with(httpBasic("admin", "password"))
        )
            .andExpect(status().isConflict)
    }
}
