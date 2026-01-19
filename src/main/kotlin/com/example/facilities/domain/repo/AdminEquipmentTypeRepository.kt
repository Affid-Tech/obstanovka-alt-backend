package com.example.facilities.domain.repo

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class AdminEquipmentTypeRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    data class EquipmentTypeRow(
        val id: UUID,
        val name: String,
        val categoryCode: String,
        val description: String?,
        val coverMediaId: UUID?
    )

    fun fetchAll(): List<EquipmentTypeRow> {
        val sql = """
            select id, name, category_code, description, cover_media_id
            from equipment_type
            order by name
        """.trimIndent()
        return jdbcTemplate.query(sql) { rs, _ ->
            EquipmentTypeRow(
                id = UUID.fromString(rs.getString("id")),
                name = rs.getString("name"),
                categoryCode = rs.getString("category_code"),
                description = rs.getString("description"),
                coverMediaId = rs.getString("cover_media_id")?.let(UUID::fromString)
            )
        }
    }

    fun fetchById(id: UUID): EquipmentTypeRow? {
        val sql = """
            select id, name, category_code, description, cover_media_id
            from equipment_type
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource("id", id)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            EquipmentTypeRow(
                id = UUID.fromString(rs.getString("id")),
                name = rs.getString("name"),
                categoryCode = rs.getString("category_code"),
                description = rs.getString("description"),
                coverMediaId = rs.getString("cover_media_id")?.let(UUID::fromString)
            )
        }.firstOrNull()
    }

    fun insert(equipmentType: EquipmentTypeRow) {
        val sql = """
            insert into equipment_type (id, name, category_code, description, cover_media_id)
            values (:id, :name, :categoryCode, :description, :coverMediaId)
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", equipmentType.id)
            .addValue("name", equipmentType.name)
            .addValue("categoryCode", equipmentType.categoryCode)
            .addValue("description", equipmentType.description)
            .addValue("coverMediaId", equipmentType.coverMediaId)
        jdbcTemplate.update(sql, params)
    }

    fun update(equipmentType: EquipmentTypeRow) {
        val sql = """
            update equipment_type
            set name = :name,
                category_code = :categoryCode,
                description = :description,
                cover_media_id = :coverMediaId
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", equipmentType.id)
            .addValue("name", equipmentType.name)
            .addValue("categoryCode", equipmentType.categoryCode)
            .addValue("description", equipmentType.description)
            .addValue("coverMediaId", equipmentType.coverMediaId)
        jdbcTemplate.update(sql, params)
    }

    fun delete(id: UUID) {
        val sql = "delete from equipment_type where id = :id"
        jdbcTemplate.update(sql, MapSqlParameterSource("id", id))
    }
}
