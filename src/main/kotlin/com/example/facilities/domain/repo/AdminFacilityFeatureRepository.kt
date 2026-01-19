package com.example.facilities.domain.repo

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Repository
class AdminFacilityFeatureRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    data class FacilityFeatureRow(
        val facilityId: UUID,
        val featureId: UUID,
        val valueBool: Boolean?,
        val valueText: String?,
        val valueNumber: BigDecimal?
    )

    fun fetchByFacility(facilityId: UUID): List<FacilityFeatureRow> {
        val sql = """
            select facility_id, feature_id, value_bool, value_text, value_number
            from facility_feature
            where facility_id = :facilityId
            order by feature_id
        """.trimIndent()
        val params = MapSqlParameterSource("facilityId", facilityId)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            FacilityFeatureRow(
                facilityId = UUID.fromString(rs.getString("facility_id")),
                featureId = UUID.fromString(rs.getString("feature_id")),
                valueBool = rs.getObject("value_bool", Boolean::class.java),
                valueText = rs.getString("value_text"),
                valueNumber = rs.getBigDecimal("value_number")
            )
        }
    }

    @Transactional
    fun replaceAllForFacility(facilityId: UUID, features: List<FacilityFeatureRow>) {
        val deleteSql = "delete from facility_feature where facility_id = :facilityId"
        jdbcTemplate.update(deleteSql, MapSqlParameterSource("facilityId", facilityId))

        if (features.isEmpty()) {
            return
        }

        val insertSql = """
            insert into facility_feature (facility_id, feature_id, value_bool, value_text, value_number)
            values (:facilityId, :featureId, :valueBool, :valueText, :valueNumber)
        """.trimIndent()
        val batchParams = features.map {
            MapSqlParameterSource()
                .addValue("facilityId", it.facilityId)
                .addValue("featureId", it.featureId)
                .addValue("valueBool", it.valueBool)
                .addValue("valueText", it.valueText)
                .addValue("valueNumber", it.valueNumber)
        }.toTypedArray()
        jdbcTemplate.batchUpdate(insertSql, batchParams)
    }
}
