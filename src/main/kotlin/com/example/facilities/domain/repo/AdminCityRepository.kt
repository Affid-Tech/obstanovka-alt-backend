package com.example.facilities.domain.repo

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.UUID

@Repository
class AdminCityRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    data class CityRow(
        val id: UUID,
        val name: String,
        val countryCode: String,
        val centerLat: BigDecimal?,
        val centerLng: BigDecimal?
    )

    fun fetchAll(): List<CityRow> {
        val sql = """
            select id, name, country_code, center_lat, center_lng
            from city
            order by name
        """.trimIndent()
        return jdbcTemplate.query(sql) { rs, _ ->
            CityRow(
                id = UUID.fromString(rs.getString("id")),
                name = rs.getString("name"),
                countryCode = rs.getString("country_code"),
                centerLat = rs.getBigDecimal("center_lat"),
                centerLng = rs.getBigDecimal("center_lng")
            )
        }
    }

    fun fetchById(id: UUID): CityRow? {
        val sql = """
            select id, name, country_code, center_lat, center_lng
            from city
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource("id", id)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            CityRow(
                id = UUID.fromString(rs.getString("id")),
                name = rs.getString("name"),
                countryCode = rs.getString("country_code"),
                centerLat = rs.getBigDecimal("center_lat"),
                centerLng = rs.getBigDecimal("center_lng")
            )
        }.firstOrNull()
    }

    fun insert(city: CityRow) {
        val sql = """
            insert into city (id, name, country_code, center_lat, center_lng)
            values (:id, :name, :countryCode, :centerLat, :centerLng)
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", city.id)
            .addValue("name", city.name)
            .addValue("countryCode", city.countryCode)
            .addValue("centerLat", city.centerLat)
            .addValue("centerLng", city.centerLng)
        jdbcTemplate.update(sql, params)
    }

    fun update(city: CityRow) {
        val sql = """
            update city
            set name = :name,
                country_code = :countryCode,
                center_lat = :centerLat,
                center_lng = :centerLng
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", city.id)
            .addValue("name", city.name)
            .addValue("countryCode", city.countryCode)
            .addValue("centerLat", city.centerLat)
            .addValue("centerLng", city.centerLng)
        jdbcTemplate.update(sql, params)
    }

    fun delete(id: UUID) {
        val sql = "delete from city where id = :id"
        jdbcTemplate.update(sql, MapSqlParameterSource("id", id))
    }
}
