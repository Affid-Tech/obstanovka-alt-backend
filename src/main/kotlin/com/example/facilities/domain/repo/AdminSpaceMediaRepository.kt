package com.example.facilities.domain.repo

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
class AdminSpaceMediaRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    data class SpaceMediaRow(
        val spaceId: UUID,
        val mediaId: UUID,
        val sortOrder: Int,
        val caption: String?
    )

    fun fetchBySpace(spaceId: UUID): List<SpaceMediaRow> {
        val sql = """
            select space_id, media_id, sort_order, caption
            from space_media
            where space_id = :spaceId
            order by sort_order
        """.trimIndent()
        val params = MapSqlParameterSource("spaceId", spaceId)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            SpaceMediaRow(
                spaceId = UUID.fromString(rs.getString("space_id")),
                mediaId = UUID.fromString(rs.getString("media_id")),
                sortOrder = rs.getInt("sort_order"),
                caption = rs.getString("caption")
            )
        }
    }

    @Transactional
    fun replaceAllForSpace(spaceId: UUID, media: List<SpaceMediaRow>) {
        val deleteSql = "delete from space_media where space_id = :spaceId"
        jdbcTemplate.update(deleteSql, MapSqlParameterSource("spaceId", spaceId))

        if (media.isEmpty()) {
            return
        }

        val insertSql = """
            insert into space_media (space_id, media_id, sort_order, caption)
            values (:spaceId, :mediaId, :sortOrder, :caption)
        """.trimIndent()
        val batchParams = media.map {
            MapSqlParameterSource()
                .addValue("spaceId", it.spaceId)
                .addValue("mediaId", it.mediaId)
                .addValue("sortOrder", it.sortOrder)
                .addValue("caption", it.caption)
        }.toTypedArray()
        jdbcTemplate.batchUpdate(insertSql, batchParams)
    }
}
