package com.example.facilities.domain.repo

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.UUID

@Repository
class AdminMediaRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    data class MediaRow(
        val id: UUID,
        val url: String,
        val kind: String,
        val createdAt: OffsetDateTime?
    )

    fun fetchById(id: UUID): MediaRow? {
        val sql = """
            select id, url, kind, created_at
            from media
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource("id", id)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            MediaRow(
                id = UUID.fromString(rs.getString("id")),
                url = rs.getString("url"),
                kind = rs.getString("kind"),
                createdAt = rs.getObject("created_at", OffsetDateTime::class.java)
            )
        }.firstOrNull()
    }

    fun insert(media: MediaRow) {
        val sql = """
            insert into media (id, url, kind)
            values (:id, :url, :kind)
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", media.id)
            .addValue("url", media.url)
            .addValue("kind", media.kind)
        jdbcTemplate.update(sql, params)
    }

    fun update(media: MediaRow) {
        val sql = """
            update media
            set url = :url,
                kind = :kind
            where id = :id
        """.trimIndent()
        val params = MapSqlParameterSource()
            .addValue("id", media.id)
            .addValue("url", media.url)
            .addValue("kind", media.kind)
        jdbcTemplate.update(sql, params)
    }

    fun delete(id: UUID) {
        val sql = "delete from media where id = :id"
        jdbcTemplate.update(sql, MapSqlParameterSource("id", id))
    }
}
