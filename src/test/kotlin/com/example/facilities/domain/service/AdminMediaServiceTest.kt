package com.example.facilities.domain.service

import com.example.facilities.api.admin.dto.AdminMediaCreate
import com.example.facilities.domain.repo.AdminMediaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class AdminMediaServiceTest {
    private val adminMediaRepository = mock(AdminMediaRepository::class.java)
    private val service = AdminMediaService(adminMediaRepository)

    @Test
    fun `createMedia rejects non-http url`() {
        val exception = assertThrows(ResponseStatusException::class.java) {
            service.createMedia(
                AdminMediaCreate(
                    url = "ftp://example.com/media.png",
                    kind = "IMAGE"
                )
            )
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
        assertEquals("media.url must be http(s)", exception.reason)
    }
}
