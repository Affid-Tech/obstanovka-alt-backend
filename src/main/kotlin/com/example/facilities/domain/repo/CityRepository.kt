package com.example.facilities.domain.repo

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.UUID

@Repository
class CityRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    data class CityRow(
        val id: UUID,
        val name: String,
        val countryCode: String,
        val centerLat: BigDecimal?,
        val centerLng: BigDecimal?
    )

    fun fetchCities(): List<CityRow> {
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
}
