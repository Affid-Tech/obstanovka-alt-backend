package com.example.facilities.domain.repo

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalTime
import java.util.UUID

@Repository
class AdminOpeningHoursRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    data class OpeningHoursRow(
        val facilityId: UUID,
        val dayOfWeek: Int,
        val isClosed: Boolean,
        val openTime: LocalTime?,
        val closeTime: LocalTime?,
        val note: String?
    )

    fun fetchByFacility(facilityId: UUID): List<OpeningHoursRow> {
        val sql = """
            select facility_id, day_of_week, is_closed, open_time, close_time, note
            from opening_hours
            where facility_id = :facilityId
            order by day_of_week
        """.trimIndent()
        val params = MapSqlParameterSource("facilityId", facilityId)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            OpeningHoursRow(
                facilityId = UUID.fromString(rs.getString("facility_id")),
                dayOfWeek = rs.getInt("day_of_week"),
                isClosed = rs.getBoolean("is_closed"),
                openTime = rs.getObject("open_time", LocalTime::class.java),
                closeTime = rs.getObject("close_time", LocalTime::class.java),
                note = rs.getString("note")
            )
        }
    }

    @Transactional
    fun replaceAllForFacility(facilityId: UUID, hours: List<OpeningHoursRow>) {
        val deleteSql = "delete from opening_hours where facility_id = :facilityId"
        jdbcTemplate.update(deleteSql, MapSqlParameterSource("facilityId", facilityId))

        if (hours.isEmpty()) {
            return
        }

        val insertSql = """
            insert into opening_hours (facility_id, day_of_week, is_closed, open_time, close_time, note)
            values (:facilityId, :dayOfWeek, :isClosed, :openTime, :closeTime, :note)
        """.trimIndent()
        val batchParams = hours.map {
            MapSqlParameterSource()
                .addValue("facilityId", it.facilityId)
                .addValue("dayOfWeek", it.dayOfWeek)
                .addValue("isClosed", it.isClosed)
                .addValue("openTime", it.openTime)
                .addValue("closeTime", it.closeTime)
                .addValue("note", it.note)
        }.toTypedArray()
        jdbcTemplate.batchUpdate(insertSql, batchParams)
    }
}
