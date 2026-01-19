package com.example.facilities.domain.repo

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class MetaRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    data class MetaRow(val code: String, val label: String)
    data class FeatureMetaRow(val code: String, val label: String, val valueType: String)

    fun fetchCapabilities(): List<MetaRow> {
        val sql = """
            select code, label
            from capability_type
            order by code
        """.trimIndent()
        return jdbcTemplate.query(sql) { rs, _ ->
            MetaRow(rs.getString("code"), rs.getString("label"))
        }
    }

    fun fetchFeatures(): List<FeatureMetaRow> {
        val sql = """
            select code, label, value_type
            from feature
            order by code
        """.trimIndent()
        return jdbcTemplate.query(sql) { rs, _ ->
            FeatureMetaRow(
                code = rs.getString("code"),
                label = rs.getString("label"),
                valueType = rs.getString("value_type")
            )
        }
    }

    fun fetchEquipmentCategories(): List<MetaRow> {
        val sql = """
            select distinct category_code as code,
                   initcap(replace(lower(category_code), '_', ' ')) as label
            from equipment_type
            order by code
        """.trimIndent()
        return jdbcTemplate.query(sql) { rs, _ ->
            MetaRow(rs.getString("code"), rs.getString("label"))
        }
    }

    fun fetchSpaceTypes(): List<MetaRow> {
        val sql = """
            select code, label
            from space_type
            order by code
        """.trimIndent()
        return jdbcTemplate.query(sql) { rs, _ ->
            MetaRow(rs.getString("code"), rs.getString("label"))
        }
    }
}
