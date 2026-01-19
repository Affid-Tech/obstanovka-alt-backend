package com.example.facilities.domain.repo

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class AdminCapabilityTypeRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    data class CapabilityTypeRow(
        val id: UUID,
        val code: String,
        val label: String
    )

    fun fetchAll(): List<CapabilityTypeRow> {
        val sql = """
            select id, code, label
            from capability_type
            order by code
        """.trimIndent()
        return jdbcTemplate.query(sql) { rs, _ ->
            CapabilityTypeRow(
                id = UUID.fromString(rs.getString("id")),
                code = rs.getString("code"),
                label = rs.getString("label")
            )
        }
    }

    fun fetchById(id: UUID): CapabilityTypeRow? {
        val sql = """
            select id, code, label
            from capability_type
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource("id", id)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            CapabilityTypeRow(
                id = UUID.fromString(rs.getString("id")),
                code = rs.getString("code"),
                label = rs.getString("label")
            )
        }.firstOrNull()
    }

    fun insert(capabilityType: CapabilityTypeRow) {
        val sql = """
            insert into capability_type (id, code, label)
            values (:id, :code, :label)
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", capabilityType.id)
            .addValue("code", capabilityType.code)
            .addValue("label", capabilityType.label)
        jdbcTemplate.update(sql, params)
    }

    fun update(capabilityType: CapabilityTypeRow) {
        val sql = """
            update capability_type
            set code = :code,
                label = :label
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", capabilityType.id)
            .addValue("code", capabilityType.code)
            .addValue("label", capabilityType.label)
        jdbcTemplate.update(sql, params)
    }

    fun delete(id: UUID) {
        val sql = "delete from capability_type where id = :id"
        jdbcTemplate.update(sql, MapSqlParameterSource("id", id))
    }
}
