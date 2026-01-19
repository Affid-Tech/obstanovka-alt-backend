package com.example.facilities.domain.repo

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
class AdminContactPointRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    data class ContactPointRow(
        val id: UUID,
        val facilityId: UUID,
        val type: String,
        val value: String,
        val label: String?,
        val isPrimary: Boolean
    )

    fun fetchByFacility(facilityId: UUID): List<ContactPointRow> {
        val sql = """
            select id, facility_id, type, value, label, is_primary
            from contact_point
            where facility_id = :facilityId
            order by is_primary desc, id
        """.trimIndent()
        val params = MapSqlParameterSource("facilityId", facilityId)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            ContactPointRow(
                id = UUID.fromString(rs.getString("id")),
                facilityId = UUID.fromString(rs.getString("facility_id")),
                type = rs.getString("type"),
                value = rs.getString("value"),
                label = rs.getString("label"),
                isPrimary = rs.getBoolean("is_primary")
            )
        }
    }

    @Transactional
    fun replaceAllForFacility(facilityId: UUID, contacts: List<ContactPointRow>) {
        val deleteSql = "delete from contact_point where facility_id = :facilityId"
        jdbcTemplate.update(deleteSql, MapSqlParameterSource("facilityId", facilityId))

        if (contacts.isEmpty()) {
            return
        }

        val insertSql = """
            insert into contact_point (id, facility_id, type, value, label, is_primary)
            values (:id, :facilityId, :type, :value, :label, :isPrimary)
        """.trimIndent()
        val batchParams = contacts.map {
            MapSqlParameterSource()
                .addValue("id", it.id)
                .addValue("facilityId", it.facilityId)
                .addValue("type", it.type)
                .addValue("value", it.value)
                .addValue("label", it.label)
                .addValue("isPrimary", it.isPrimary)
        }.toTypedArray()
        jdbcTemplate.batchUpdate(insertSql, batchParams)
    }
}
