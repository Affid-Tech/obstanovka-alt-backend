package com.example.facilities.domain.repo

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class AdminFeatureRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    data class FeatureRow(
        val id: UUID,
        val code: String,
        val label: String,
        val valueType: String
    )

    fun fetchAll(): List<FeatureRow> {
        val sql = """
            select id, code, label, value_type
            from feature
            order by code
        """.trimIndent()
        return jdbcTemplate.query(sql) { rs, _ ->
            FeatureRow(
                id = UUID.fromString(rs.getString("id")),
                code = rs.getString("code"),
                label = rs.getString("label"),
                valueType = rs.getString("value_type")
            )
        }
    }

    fun fetchById(id: UUID): FeatureRow? {
        val sql = """
            select id, code, label, value_type
            from feature
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource("id", id)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            FeatureRow(
                id = UUID.fromString(rs.getString("id")),
                code = rs.getString("code"),
                label = rs.getString("label"),
                valueType = rs.getString("value_type")
            )
        }.firstOrNull()
    }

    fun insert(feature: FeatureRow) {
        val sql = """
            insert into feature (id, code, label, value_type)
            values (:id, :code, :label, :valueType)
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", feature.id)
            .addValue("code", feature.code)
            .addValue("label", feature.label)
            .addValue("valueType", feature.valueType)
        jdbcTemplate.update(sql, params)
    }

    fun update(feature: FeatureRow) {
        val sql = """
            update feature
            set code = :code,
                label = :label,
                value_type = :valueType
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", feature.id)
            .addValue("code", feature.code)
            .addValue("label", feature.label)
            .addValue("valueType", feature.valueType)
        jdbcTemplate.update(sql, params)
    }

    fun delete(id: UUID) {
        val sql = "delete from feature where id = :id"
        jdbcTemplate.update(sql, MapSqlParameterSource("id", id))
    }

    fun isReferenced(id: UUID): Boolean {
        val sql = """
            select exists(
                select 1 from facility_feature where feature_id = :id
            )
        """.trimIndent()
        return jdbcTemplate.queryForObject(sql, MapSqlParameterSource("id", id), Boolean::class.java) ?: false
    }
}
