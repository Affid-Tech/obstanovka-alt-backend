package com.example.facilities.domain.repo

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
class AdminFacilityEquipmentRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    data class FacilityEquipmentRow(
        val facilityId: UUID,
        val equipmentTypeId: UUID,
        val quantity: Int?,
        val mode: String,
        val note: String?
    )

    fun fetchByFacility(facilityId: UUID): List<FacilityEquipmentRow> {
        val sql = """
            select facility_id, equipment_type_id, quantity, mode, note
            from facility_equipment
            where facility_id = :facilityId
            order by equipment_type_id
        """.trimIndent()
        val params = MapSqlParameterSource("facilityId", facilityId)
        return jdbcTemplate.query(sql, params) { rs, _ ->
            FacilityEquipmentRow(
                facilityId = UUID.fromString(rs.getString("facility_id")),
                equipmentTypeId = UUID.fromString(rs.getString("equipment_type_id")),
                quantity = rs.getObject("quantity", Int::class.java),
                mode = rs.getString("mode"),
                note = rs.getString("note")
            )
        }
    }

    @Transactional
    fun replaceAllForFacility(facilityId: UUID, equipment: List<FacilityEquipmentRow>) {
        val deleteSql = "delete from facility_equipment where facility_id = :facilityId"
        jdbcTemplate.update(deleteSql, MapSqlParameterSource("facilityId", facilityId))

        if (equipment.isEmpty()) {
            return
        }

        val insertSql = """
            insert into facility_equipment (facility_id, equipment_type_id, quantity, mode, note)
            values (:facilityId, :equipmentTypeId, :quantity, :mode, :note)
        """.trimIndent()
        val batchParams = equipment.map {
            MapSqlParameterSource()
                .addValue("facilityId", it.facilityId)
                .addValue("equipmentTypeId", it.equipmentTypeId)
                .addValue("quantity", it.quantity)
                .addValue("mode", it.mode)
                .addValue("note", it.note)
        }.toTypedArray()
        jdbcTemplate.batchUpdate(insertSql, batchParams)
    }
}
