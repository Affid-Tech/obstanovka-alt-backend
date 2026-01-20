package com.example.facilities.domain.repo

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.UUID

@Repository
class AdminFacilityRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    data class FacilityRow(
        val id: UUID,
        val cityId: UUID,
        val name: String,
        val description: String?,
        val addressId: UUID?,
        val coverMediaId: UUID?,
        val status: String,
        val createdAt: OffsetDateTime? = null,
        val updatedAt: OffsetDateTime? = null
    )

    fun fetchById(id: UUID): FacilityRow? {
        val sql = """
            select id, city_id, name, description, address_id, cover_media_id, status, created_at, updated_at
            from facility
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource("id", id)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            FacilityRow(
                id = UUID.fromString(rs.getString("id")),
                cityId = UUID.fromString(rs.getString("city_id")),
                name = rs.getString("name"),
                description = rs.getString("description"),
                addressId = rs.getString("address_id")?.let(UUID::fromString),
                coverMediaId = rs.getString("cover_media_id")?.let(UUID::fromString),
                status = rs.getString("status"),
                createdAt = rs.getObject("created_at", OffsetDateTime::class.java),
                updatedAt = rs.getObject("updated_at", OffsetDateTime::class.java)
            )
        }.firstOrNull()
    }

    fun insert(facility: FacilityRow) {
        val sql = """
            insert into facility (id, city_id, name, description, address_id, cover_media_id, status)
            values (:id, :cityId, :name, :description, :addressId, :coverMediaId, :status)
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", facility.id)
            .addValue("cityId", facility.cityId)
            .addValue("name", facility.name)
            .addValue("description", facility.description)
            .addValue("addressId", facility.addressId)
            .addValue("coverMediaId", facility.coverMediaId)
            .addValue("status", facility.status)
        jdbcTemplate.update(sql, params)
    }

    fun update(facility: FacilityRow) {
        val sql = """
            update facility
            set city_id = :cityId,
                name = :name,
                description = :description,
                address_id = :addressId,
                cover_media_id = :coverMediaId,
                status = :status,
                updated_at = now()
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", facility.id)
            .addValue("cityId", facility.cityId)
            .addValue("name", facility.name)
            .addValue("description", facility.description)
            .addValue("addressId", facility.addressId)
            .addValue("coverMediaId", facility.coverMediaId)
            .addValue("status", facility.status)
        jdbcTemplate.update(sql, params)
    }

    fun delete(id: UUID) {
        val sql = "delete from facility where id = :id"
        jdbcTemplate.update(sql, MapSqlParameterSource("id", id))
    }
}
