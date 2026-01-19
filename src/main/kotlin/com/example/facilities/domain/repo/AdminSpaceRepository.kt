package com.example.facilities.domain.repo

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.UUID

@Repository
class AdminSpaceRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    data class SpaceRow(
        val id: UUID,
        val facilityId: UUID,
        val spaceTypeId: UUID,
        val name: String,
        val description: String?,
        val capacityPeople: Int?,
        val sizeM2: BigDecimal?
    )

    fun fetchById(id: UUID): SpaceRow? {
        val sql = """
            select id, facility_id, space_type_id, name, description, capacity_people, size_m2
            from space
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource("id", id)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            SpaceRow(
                id = UUID.fromString(rs.getString("id")),
                facilityId = UUID.fromString(rs.getString("facility_id")),
                spaceTypeId = UUID.fromString(rs.getString("space_type_id")),
                name = rs.getString("name"),
                description = rs.getString("description"),
                capacityPeople = rs.getObject("capacity_people", Int::class.java),
                sizeM2 = rs.getBigDecimal("size_m2")
            )
        }.firstOrNull()
    }

    fun fetchByFacility(facilityId: UUID): List<SpaceRow> {
        val sql = """
            select id, facility_id, space_type_id, name, description, capacity_people, size_m2
            from space
            where facility_id = :facilityId
            order by name
        """.trimIndent()
        val params = MapSqlParameterSource("facilityId", facilityId)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            SpaceRow(
                id = UUID.fromString(rs.getString("id")),
                facilityId = UUID.fromString(rs.getString("facility_id")),
                spaceTypeId = UUID.fromString(rs.getString("space_type_id")),
                name = rs.getString("name"),
                description = rs.getString("description"),
                capacityPeople = rs.getObject("capacity_people", Int::class.java),
                sizeM2 = rs.getBigDecimal("size_m2")
            )
        }
    }

    fun insert(space: SpaceRow) {
        val sql = """
            insert into space (id, facility_id, space_type_id, name, description, capacity_people, size_m2)
            values (:id, :facilityId, :spaceTypeId, :name, :description, :capacityPeople, :sizeM2)
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", space.id)
            .addValue("facilityId", space.facilityId)
            .addValue("spaceTypeId", space.spaceTypeId)
            .addValue("name", space.name)
            .addValue("description", space.description)
            .addValue("capacityPeople", space.capacityPeople)
            .addValue("sizeM2", space.sizeM2)
        jdbcTemplate.update(sql, params)
    }

    fun update(space: SpaceRow) {
        val sql = """
            update space
            set facility_id = :facilityId,
                space_type_id = :spaceTypeId,
                name = :name,
                description = :description,
                capacity_people = :capacityPeople,
                size_m2 = :sizeM2
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", space.id)
            .addValue("facilityId", space.facilityId)
            .addValue("spaceTypeId", space.spaceTypeId)
            .addValue("name", space.name)
            .addValue("description", space.description)
            .addValue("capacityPeople", space.capacityPeople)
            .addValue("sizeM2", space.sizeM2)
        jdbcTemplate.update(sql, params)
    }

    fun delete(id: UUID) {
        val sql = "delete from space where id = :id"
        jdbcTemplate.update(sql, MapSqlParameterSource("id", id))
    }
}
