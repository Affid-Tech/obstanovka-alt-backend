package com.example.facilities.domain.repo

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
class AdminFacilityMediaRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    data class FacilityMediaRow(
        val facilityId: UUID,
        val mediaId: UUID,
        val sortOrder: Int,
        val caption: String?,
        val isCover: Boolean
    )

    fun fetchByFacility(facilityId: UUID): List<FacilityMediaRow> {
        val sql = """
            select facility_id, media_id, sort_order, caption, is_cover
            from facility_media
            where facility_id = :facilityId
            order by sort_order
        """.trimIndent()
        val params = MapSqlParameterSource("facilityId", facilityId)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            FacilityMediaRow(
                facilityId = UUID.fromString(rs.getString("facility_id")),
                mediaId = UUID.fromString(rs.getString("media_id")),
                sortOrder = rs.getInt("sort_order"),
                caption = rs.getString("caption"),
                isCover = rs.getBoolean("is_cover")
            )
        }
    }

    @Transactional
    fun replaceAllForFacility(facilityId: UUID, media: List<FacilityMediaRow>) {
        val deleteSql = "delete from facility_media where facility_id = :facilityId"
        jdbcTemplate.update(deleteSql, MapSqlParameterSource("facilityId", facilityId))

        if (media.isEmpty()) {
            return
        }

        val insertSql = """
            insert into facility_media (facility_id, media_id, sort_order, caption, is_cover)
            values (:facilityId, :mediaId, :sortOrder, :caption, :isCover)
        """.trimIndent()
        val batchParams = media.map {
            MapSqlParameterSource()
                .addValue("facilityId", it.facilityId)
                .addValue("mediaId", it.mediaId)
                .addValue("sortOrder", it.sortOrder)
                .addValue("caption", it.caption)
                .addValue("isCover", it.isCover)
        }.toTypedArray()
        jdbcTemplate.batchUpdate(insertSql, batchParams)
    }
}
