package com.example.facilities.domain.repo

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Repository
class AdminPriceInfoRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    data class PriceInfoRow(
        val id: UUID,
        val facilityId: UUID,
        val capabilityTypeId: UUID?,
        val spaceId: UUID?,
        val kind: String,
        val amountFrom: BigDecimal?,
        val amountTo: BigDecimal?,
        val currency: String,
        val note: String?
    )

    fun fetchById(id: UUID): PriceInfoRow? {
        val sql = """
            select id, facility_id, capability_type_id, space_id, kind, amount_from, amount_to, currency, note
            from price_info
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource("id", id)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            PriceInfoRow(
                id = UUID.fromString(rs.getString("id")),
                facilityId = UUID.fromString(rs.getString("facility_id")),
                capabilityTypeId = rs.getString("capability_type_id")?.let(UUID::fromString),
                spaceId = rs.getString("space_id")?.let(UUID::fromString),
                kind = rs.getString("kind"),
                amountFrom = rs.getBigDecimal("amount_from"),
                amountTo = rs.getBigDecimal("amount_to"),
                currency = rs.getString("currency"),
                note = rs.getString("note")
            )
        }.firstOrNull()
    }

    fun fetchByFacility(facilityId: UUID): List<PriceInfoRow> {
        val sql = """
            select id, facility_id, capability_type_id, space_id, kind, amount_from, amount_to, currency, note
            from price_info
            where facility_id = :facilityId
            order by id
        """.trimIndent()
        val params = MapSqlParameterSource("facilityId", facilityId)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            PriceInfoRow(
                id = UUID.fromString(rs.getString("id")),
                facilityId = UUID.fromString(rs.getString("facility_id")),
                capabilityTypeId = rs.getString("capability_type_id")?.let(UUID::fromString),
                spaceId = rs.getString("space_id")?.let(UUID::fromString),
                kind = rs.getString("kind"),
                amountFrom = rs.getBigDecimal("amount_from"),
                amountTo = rs.getBigDecimal("amount_to"),
                currency = rs.getString("currency"),
                note = rs.getString("note")
            )
        }
    }

    fun insert(priceInfo: PriceInfoRow) {
        val sql = """
            insert into price_info (
                id,
                facility_id,
                capability_type_id,
                space_id,
                kind,
                amount_from,
                amount_to,
                currency,
                note
            )
            values (
                :id,
                :facilityId,
                :capabilityTypeId,
                :spaceId,
                :kind,
                :amountFrom,
                :amountTo,
                :currency,
                :note
            )
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", priceInfo.id)
            .addValue("facilityId", priceInfo.facilityId)
            .addValue("capabilityTypeId", priceInfo.capabilityTypeId)
            .addValue("spaceId", priceInfo.spaceId)
            .addValue("kind", priceInfo.kind)
            .addValue("amountFrom", priceInfo.amountFrom)
            .addValue("amountTo", priceInfo.amountTo)
            .addValue("currency", priceInfo.currency)
            .addValue("note", priceInfo.note)
        jdbcTemplate.update(sql, params)
    }

    fun update(priceInfo: PriceInfoRow) {
        val sql = """
            update price_info
            set facility_id = :facilityId,
                capability_type_id = :capabilityTypeId,
                space_id = :spaceId,
                kind = :kind,
                amount_from = :amountFrom,
                amount_to = :amountTo,
                currency = :currency,
                note = :note
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", priceInfo.id)
            .addValue("facilityId", priceInfo.facilityId)
            .addValue("capabilityTypeId", priceInfo.capabilityTypeId)
            .addValue("spaceId", priceInfo.spaceId)
            .addValue("kind", priceInfo.kind)
            .addValue("amountFrom", priceInfo.amountFrom)
            .addValue("amountTo", priceInfo.amountTo)
            .addValue("currency", priceInfo.currency)
            .addValue("note", priceInfo.note)
        jdbcTemplate.update(sql, params)
    }

    fun delete(id: UUID) {
        val sql = "delete from price_info where id = :id"
        jdbcTemplate.update(sql, MapSqlParameterSource("id", id))
    }

    @Transactional
    fun replaceAllForFacility(facilityId: UUID, prices: List<PriceInfoRow>) {
        val deleteSql = "delete from price_info where facility_id = :facilityId"
        jdbcTemplate.update(deleteSql, MapSqlParameterSource("facilityId", facilityId))

        if (prices.isEmpty()) {
            return
        }

        val insertSql = """
            insert into price_info (
                id,
                facility_id,
                capability_type_id,
                space_id,
                kind,
                amount_from,
                amount_to,
                currency,
                note
            )
            values (
                :id,
                :facilityId,
                :capabilityTypeId,
                :spaceId,
                :kind,
                :amountFrom,
                :amountTo,
                :currency,
                :note
            )
        """.trimIndent()
        val batchParams = prices.map {
            MapSqlParameterSource()
                .addValue("id", it.id)
                .addValue("facilityId", it.facilityId)
                .addValue("capabilityTypeId", it.capabilityTypeId)
                .addValue("spaceId", it.spaceId)
                .addValue("kind", it.kind)
                .addValue("amountFrom", it.amountFrom)
                .addValue("amountTo", it.amountTo)
                .addValue("currency", it.currency)
                .addValue("note", it.note)
        }.toTypedArray()
        jdbcTemplate.batchUpdate(insertSql, batchParams)
    }
}
