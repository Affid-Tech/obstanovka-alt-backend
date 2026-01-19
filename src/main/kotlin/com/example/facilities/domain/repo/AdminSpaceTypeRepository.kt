package com.example.facilities.domain.repo

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class AdminSpaceTypeRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    data class SpaceTypeRow(
        val id: UUID,
        val code: String,
        val label: String
    )

    fun fetchAll(): List<SpaceTypeRow> {
        val sql = """
            select id, code, label
            from space_type
            order by code
        """.trimIndent()
        return jdbcTemplate.query(sql) { rs, _ ->
            SpaceTypeRow(
                id = UUID.fromString(rs.getString("id")),
                code = rs.getString("code"),
                label = rs.getString("label")
            )
        }
    }

    fun fetchById(id: UUID): SpaceTypeRow? {
        val sql = """
            select id, code, label
            from space_type
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource("id", id)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            SpaceTypeRow(
                id = UUID.fromString(rs.getString("id")),
                code = rs.getString("code"),
                label = rs.getString("label")
            )
        }.firstOrNull()
    }

    fun insert(spaceType: SpaceTypeRow) {
        val sql = """
            insert into space_type (id, code, label)
            values (:id, :code, :label)
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", spaceType.id)
            .addValue("code", spaceType.code)
            .addValue("label", spaceType.label)
        jdbcTemplate.update(sql, params)
    }

    fun update(spaceType: SpaceTypeRow) {
        val sql = """
            update space_type
            set code = :code,
                label = :label
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", spaceType.id)
            .addValue("code", spaceType.code)
            .addValue("label", spaceType.label)
        jdbcTemplate.update(sql, params)
    }

    fun delete(id: UUID) {
        val sql = "delete from space_type where id = :id"
        jdbcTemplate.update(sql, MapSqlParameterSource("id", id))
    }
}
