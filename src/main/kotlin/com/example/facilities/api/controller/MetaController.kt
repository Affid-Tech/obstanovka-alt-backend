package com.example.facilities.api.controller

import com.example.facilities.api.dto.MetaResponse
import com.example.facilities.domain.service.MetaService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/meta")
class MetaController(
    private val metaService: MetaService
) {
    @GetMapping
    fun getMeta(): MetaResponse = MetaResponse(
        capabilities = metaService.loadCapabilities(),
        features = metaService.loadFeatures(),
        equipmentCategories = metaService.loadEquipmentCategories(),
        spaceTypes = metaService.loadSpaceTypes()
    )
}
