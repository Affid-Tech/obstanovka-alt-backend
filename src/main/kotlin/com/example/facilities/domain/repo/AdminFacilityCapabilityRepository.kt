package com.example.facilities.domain.repo

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
class AdminFacilityCapabilityRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    data class FacilityCapabilityRow(
        val facilityId: UUID,
        val capabilityTypeId: UUID,
        val summary: String?,
        val detailsJson: String?,
        val isActive: Boolean
    )

    fun fetchByFacility(facilityId: UUID): List<FacilityCapabilityRow> {
        val sql = """
            select facility_id, capability_type_id, summary, details_json, is_active
            from facility_capability
            where facility_id = :facilityId
            order by capability_type_id
        """.trimIndent()
        val params = MapSqlParameterSource("facilityId", facilityId)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            FacilityCapabilityRow(
                facilityId = UUID.fromString(rs.getString("facility_id")),
                capabilityTypeId = UUID.fromString(rs.getString("capability_type_id")),
                summary = rs.getString("summary"),
                detailsJson = rs.getString("details_json"),
                isActive = rs.getBoolean("is_active")
            )
        }
    }

    @Transactional
    fun replaceAllForFacility(facilityId: UUID, capabilities: List<FacilityCapabilityRow>) {
        val deleteSql = "delete from facility_capability where facility_id = :facilityId"
        jdbcTemplate.update(deleteSql, MapSqlParameterSource("facilityId", facilityId))

        if (capabilities.isEmpty()) {
            return
        }

        val insertSql = """
            insert into facility_capability (
                facility_id,
                capability_type_id,
                summary,
                details_json,
                is_active
            )
            values (
                :facilityId,
                :capabilityTypeId,
                :summary,
                cast(:detailsJson as jsonb),
                :isActive
            )
        """.trimIndent()
        val batchParams = capabilities.map {
            MapSqlParameterSource()
                .addValue("facilityId", it.facilityId)
                .addValue("capabilityTypeId", it.capabilityTypeId)
                .addValue("summary", it.summary)
                .addValue("detailsJson", it.detailsJson)
                .addValue("isActive", it.isActive)
        }.toTypedArray()
        jdbcTemplate.batchUpdate(insertSql, batchParams)
    }
}
