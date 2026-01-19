package com.example.facilities.domain.service

import com.example.facilities.api.dto.FeatureMetaDTO
import com.example.facilities.api.dto.MetaItemDTO
import com.example.facilities.domain.repo.MetaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MetaService(
    private val metaRepository: MetaRepository
) {
    @Transactional(readOnly = true)
    fun loadCapabilities(): List<MetaItemDTO> = metaRepository.fetchCapabilities().map { MetaItemDTO(it.code, it.label) }

    @Transactional(readOnly = true)
    fun loadFeatures(): List<FeatureMetaDTO> = metaRepository.fetchFeatures().map {
        FeatureMetaDTO(it.code, it.label, it.valueType)
    }

    @Transactional(readOnly = true)
    fun loadEquipmentCategories(): List<MetaItemDTO> = metaRepository.fetchEquipmentCategories().map {
        MetaItemDTO(it.code, it.label)
    }

    @Transactional(readOnly = true)
    fun loadSpaceTypes(): List<MetaItemDTO> = metaRepository.fetchSpaceTypes().map {
        MetaItemDTO(it.code, it.label)
    }
}
