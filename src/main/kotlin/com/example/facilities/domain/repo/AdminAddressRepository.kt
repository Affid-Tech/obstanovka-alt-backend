package com.example.facilities.domain.repo

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.UUID

@Repository
class AdminAddressRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    data class AddressRow(
        val id: UUID,
        val cityId: UUID,
        val label: String,
        val lat: BigDecimal?,
        val lng: BigDecimal?
    )

    fun fetchById(id: UUID): AddressRow? {
        val sql = """
            select id, city_id, label, lat, lng
            from address
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource("id", id)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            AddressRow(
                id = UUID.fromString(rs.getString("id")),
                cityId = UUID.fromString(rs.getString("city_id")),
                label = rs.getString("label"),
                lat = rs.getBigDecimal("lat"),
                lng = rs.getBigDecimal("lng")
            )
        }.firstOrNull()
    }

    fun insert(address: AddressRow) {
        val sql = """
            insert into address (id, city_id, label, lat, lng)
            values (:id, :cityId, :label, :lat, :lng)
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", address.id)
            .addValue("cityId", address.cityId)
            .addValue("label", address.label)
            .addValue("lat", address.lat)
            .addValue("lng", address.lng)
        jdbcTemplate.update(sql, params)
    }

    fun update(address: AddressRow) {
        val sql = """
            update address
            set city_id = :cityId,
                label = :label,
                lat = :lat,
                lng = :lng
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", address.id)
            .addValue("cityId", address.cityId)
            .addValue("label", address.label)
            .addValue("lat", address.lat)
            .addValue("lng", address.lng)
        jdbcTemplate.update(sql, params)
    }

    fun delete(id: UUID) {
        val sql = "delete from address where id = :id"
        jdbcTemplate.update(sql, MapSqlParameterSource("id", id))
    }
}
