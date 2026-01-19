package com.example.facilities.api.controller

import com.example.facilities.api.dto.CityListResponse
import com.example.facilities.domain.service.CityService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/cities")
class CityController(
    private val cityService: CityService
) {
    @GetMapping
    fun listCities(): CityListResponse = CityListResponse(items = cityService.listCities())
}
