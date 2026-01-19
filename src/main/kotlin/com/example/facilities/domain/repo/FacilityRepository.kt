package com.example.facilities.domain.repo

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.ResultSet
import java.util.UUID

@Repository
class FacilityRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper
) {
    data class FacilityBaseRow(
        val id: UUID,
        val name: String,
        val description: String?,
        val cityId: UUID,
        val cityName: String,
        val addressLabel: String?,
        val lat: BigDecimal?,
        val lng: BigDecimal?,
        val coverImageUrl: String?
    )

    data class CapabilityRow(
        val facilityId: UUID,
        val code: String,
        val label: String
    )

    data class FeatureRow(
        val facilityId: UUID,
        val code: String,
        val label: String,
        val valueBool: Boolean?,
        val valueText: String?,
        val valueNumber: BigDecimal?
    )

    data class EquipmentRow(
        val facilityId: UUID,
        val categoryCode: String,
        val name: String,
        val quantity: Int?,
        val mode: String,
        val note: String?
    )

    data class PriceInfoRow(
        val facilityId: UUID,
        val kind: String,
        val amountFrom: BigDecimal?,
        val amountTo: BigDecimal?,
        val currency: String,
        val note: String?
    )

    data class ContactRow(
        val type: String,
        val value: String,
        val label: String?,
        val isPrimary: Boolean
    )

    data class OpeningHoursRow(
        val dayOfWeek: Int,
        val isClosed: Boolean,
        val openTime: String?,
        val closeTime: String?,
        val note: String?
    )

    data class MediaRow(
        val url: String,
        val caption: String?,
        val sortOrder: Int
    )

    data class CapabilityDetailsRow(
        val code: String,
        val label: String,
        val summary: String?,
        val details: JsonNode?
    )

    data class SpaceRow(
        val id: UUID,
        val name: String,
        val typeCode: String,
        val capacityPeople: Int?,
        val sizeM2: BigDecimal?,
        val description: String?
    )

    data class SpaceMediaRow(
        val spaceId: UUID,
        val url: String,
        val caption: String?,
        val sortOrder: Int
    )

    fun findFacilityIds(
        cityId: UUID,
        query: String?,
        capabilityCodes: List<String>,
        featureCodes: List<String>,
        equipmentCategories: List<String>,
        spaceTypes: List<String>,
        hasAddress: Boolean?,
        hasCoordinates: Boolean?,
        priceMin: BigDecimal?,
        priceMax: BigDecimal?,
        sort: String,
        page: Int,
        pageSize: Int
    ): List<UUID> {
        val params = MapSqlParameterSource()
        params.addValue("cityId", cityId)
        params.addValue("query", query)
        params.addValue("capabilityCodes", capabilityCodes)
        params.addValue("featureCodes", featureCodes)
        params.addValue("featureCount", featureCodes.size)
        params.addValue("equipmentCategories", equipmentCategories)
        params.addValue("spaceTypes", spaceTypes)
        params.addValue("hasAddress", hasAddress)
        params.addValue("hasCoordinates", hasCoordinates)
        params.addValue("priceMin", priceMin)
        params.addValue("priceMax", priceMax)
        params.addValue("limit", pageSize)
        params.addValue("offset", (page - 1) * pageSize)

        val sql = StringBuilder(
            """
            select f.id
            from facility f
            left join address a on f.address_id = a.id
            where f.city_id = :cityId
              and f.status = 'ACTIVE'
            """.trimIndent()
        )

        if (!query.isNullOrBlank()) {
            sql.append("\n  and to_tsvector('simple', coalesce(f.name,'') || ' ' || coalesce(f.description,'')) @@ plainto_tsquery('simple', :query)")
        }

        if (capabilityCodes.isNotEmpty()) {
            sql.append(
                """
                \n  and exists (
                    select 1
                    from facility_capability fc
                    join capability_type ct on ct.id = fc.capability_type_id
                    where fc.facility_id = f.id
                      and fc.is_active = true
                      and ct.code in (:capabilityCodes)
                )
                """.trimIndent()
            )
        }

        if (featureCodes.isNotEmpty()) {
            sql.append(
                """
                \n  and exists (
                    select ff.facility_id
                    from facility_feature ff
                    join feature fe on fe.id = ff.feature_id
                    where ff.facility_id = f.id
                      and fe.code in (:featureCodes)
                    group by ff.facility_id
                    having count(distinct fe.code) = :featureCount
                )
                """.trimIndent()
            )
        }

        if (equipmentCategories.isNotEmpty()) {
            sql.append(
                """
                \n  and exists (
                    select 1
                    from facility_equipment feq
                    join equipment_type et on et.id = feq.equipment_type_id
                    where feq.facility_id = f.id
                      and et.category_code in (:equipmentCategories)
                )
                """.trimIndent()
            )
        }

        if (spaceTypes.isNotEmpty()) {
            sql.append(
                """
                \n  and exists (
                    select 1
                    from space s
                    join space_type st on st.id = s.space_type_id
                    where s.facility_id = f.id
                      and st.code in (:spaceTypes)
                )
                """.trimIndent()
            )
        }

        if (hasAddress != null) {
            sql.append("\n  and (f.address_id is ${if (hasAddress) "not" else ""} null)")
        }

        if (hasCoordinates != null) {
            val clause = if (hasCoordinates) "a.lat is not null and a.lng is not null" else "(a.lat is null or a.lng is null)"
            sql.append("\n  and $clause")
        }

        if (priceMin != null || priceMax != null) {
            val priceClauses = mutableListOf<String>()
            if (priceMin != null) {
                priceClauses.add("coalesce(pi.amount_to, pi.amount_from) >= :priceMin")
            }
            if (priceMax != null) {
                priceClauses.add("coalesce(pi.amount_from, pi.amount_to) <= :priceMax")
            }
            sql.append(
                """
                \n  and exists (
                    select 1
                    from price_info pi
                    where pi.facility_id = f.id
                      and pi.kind <> 'CONTACT'
                      and ${priceClauses.joinToString(" and ")}
                )
                """.trimIndent()
            )
        }

        when (sort) {
            "PRICE_ASC" -> sql.append(
                """
                \norder by (
                    select min(coalesce(pi.amount_from, pi.amount_to))
                    from price_info pi
                    where pi.facility_id = f.id
                      and pi.kind <> 'CONTACT'
                ) asc nulls last, lower(f.name) asc
                """.trimIndent()
            )

            "COORDINATES_FIRST" -> sql.append("\norder by case when a.lat is not null and a.lng is not null then 0 else 1 end asc, lower(f.name) asc")

            "NAME_ASC" -> sql.append("\norder by lower(f.name) asc")

            else -> sql.append("\norder by lower(f.name) asc")
        }

        sql.append("\nlimit :limit offset :offset")

        return jdbcTemplate.query(sql.toString(), params) { rs, _ -> UUID.fromString(rs.getString("id")) }
    }

    fun countFacilities(
        cityId: UUID,
        query: String?,
        capabilityCodes: List<String>,
        featureCodes: List<String>,
        equipmentCategories: List<String>,
        spaceTypes: List<String>,
        hasAddress: Boolean?,
        hasCoordinates: Boolean?,
        priceMin: BigDecimal?,
        priceMax: BigDecimal?
    ): Long {
        val params = MapSqlParameterSource()
        params.addValue("cityId", cityId)
        params.addValue("query", query)
        params.addValue("capabilityCodes", capabilityCodes)
        params.addValue("featureCodes", featureCodes)
        params.addValue("featureCount", featureCodes.size)
        params.addValue("equipmentCategories", equipmentCategories)
        params.addValue("spaceTypes", spaceTypes)
        params.addValue("hasAddress", hasAddress)
        params.addValue("hasCoordinates", hasCoordinates)
        params.addValue("priceMin", priceMin)
        params.addValue("priceMax", priceMax)

        val sql = StringBuilder(
            """
            select count(distinct f.id) as total
            from facility f
            left join address a on f.address_id = a.id
            where f.city_id = :cityId
              and f.status = 'ACTIVE'
            """.trimIndent()
        )

        if (!query.isNullOrBlank()) {
            sql.append("\n  and to_tsvector('simple', coalesce(f.name,'') || ' ' || coalesce(f.description,'')) @@ plainto_tsquery('simple', :query)")
        }

        if (capabilityCodes.isNotEmpty()) {
            sql.append(
                """
                \n  and exists (
                    select 1
                    from facility_capability fc
                    join capability_type ct on ct.id = fc.capability_type_id
                    where fc.facility_id = f.id
                      and fc.is_active = true
                      and ct.code in (:capabilityCodes)
                )
                """.trimIndent()
            )
        }

        if (featureCodes.isNotEmpty()) {
            sql.append(
                """
                \n  and exists (
                    select ff.facility_id
                    from facility_feature ff
                    join feature fe on fe.id = ff.feature_id
                    where ff.facility_id = f.id
                      and fe.code in (:featureCodes)
                    group by ff.facility_id
                    having count(distinct fe.code) = :featureCount
                )
                """.trimIndent()
            )
        }

        if (equipmentCategories.isNotEmpty()) {
            sql.append(
                """
                \n  and exists (
                    select 1
                    from facility_equipment feq
                    join equipment_type et on et.id = feq.equipment_type_id
                    where feq.facility_id = f.id
                      and et.category_code in (:equipmentCategories)
                )
                """.trimIndent()
            )
        }

        if (spaceTypes.isNotEmpty()) {
            sql.append(
                """
                \n  and exists (
                    select 1
                    from space s
                    join space_type st on st.id = s.space_type_id
                    where s.facility_id = f.id
                      and st.code in (:spaceTypes)
                )
                """.trimIndent()
            )
        }

        if (hasAddress != null) {
            sql.append("\n  and (f.address_id is ${if (hasAddress) "not" else ""} null)")
        }

        if (hasCoordinates != null) {
            val clause = if (hasCoordinates) "a.lat is not null and a.lng is not null" else "(a.lat is null or a.lng is null)"
            sql.append("\n  and $clause")
        }

        if (priceMin != null || priceMax != null) {
            val priceClauses = mutableListOf<String>()
            if (priceMin != null) {
                priceClauses.add("coalesce(pi.amount_to, pi.amount_from) >= :priceMin")
            }
            if (priceMax != null) {
                priceClauses.add("coalesce(pi.amount_from, pi.amount_to) <= :priceMax")
            }
            sql.append(
                """
                \n  and exists (
                    select 1
                    from price_info pi
                    where pi.facility_id = f.id
                      and pi.kind <> 'CONTACT'
                      and ${priceClauses.joinToString(" and ")}
                )
                """.trimIndent()
            )
        }

        return jdbcTemplate.queryForObject(sql.toString(), params, Long::class.java) ?: 0
    }

    fun fetchFacilitiesBase(ids: List<UUID>): List<FacilityBaseRow> {
        if (ids.isEmpty()) return emptyList()
        val params = MapSqlParameterSource("ids", ids)
        val sql = """
            select f.id,
                   f.name,
                   f.description,
                   c.id as city_id,
                   c.name as city_name,
                   a.label as address_label,
                   a.lat,
                   a.lng,
                   m.url as cover_image_url
            from facility f
            join city c on c.id = f.city_id
            left join address a on a.id = f.address_id
            left join media m on m.id = f.cover_media_id
            where f.id in (:ids)
        """.trimIndent()
        return jdbcTemplate.query(sql, params, RowMapper { rs, _ -> mapFacilityBaseRow(rs) })
    }

    fun fetchCapabilities(ids: List<UUID>): List<CapabilityRow> {
        if (ids.isEmpty()) return emptyList()
        val params = MapSqlParameterSource("ids", ids)
        val sql = """
            select fc.facility_id,
                   ct.code,
                   ct.label
            from facility_capability fc
            join capability_type ct on ct.id = fc.capability_type_id
            where fc.facility_id in (:ids)
              and fc.is_active = true
        """.trimIndent()
        return jdbcTemplate.query(sql, params) { rs, _ ->
            CapabilityRow(
                facilityId = UUID.fromString(rs.getString("facility_id")),
                code = rs.getString("code"),
                label = rs.getString("label")
            )
        }
    }

    fun fetchFeatureCodes(ids: List<UUID>): List<Pair<UUID, String>> {
        if (ids.isEmpty()) return emptyList()
        val params = MapSqlParameterSource("ids", ids)
        val sql = """
            select ff.facility_id,
                   fe.code
            from facility_feature ff
            join feature fe on fe.id = ff.feature_id
            where ff.facility_id in (:ids)
        """.trimIndent()
        return jdbcTemplate.query(sql, params) { rs, _ ->
            UUID.fromString(rs.getString("facility_id")) to rs.getString("code")
        }
    }

    fun fetchEquipmentCategories(ids: List<UUID>): List<Pair<UUID, String>> {
        if (ids.isEmpty()) return emptyList()
        val params = MapSqlParameterSource("ids", ids)
        val sql = """
            select fe.facility_id,
                   et.category_code
            from facility_equipment fe
            join equipment_type et on et.id = fe.equipment_type_id
            where fe.facility_id in (:ids)
        """.trimIndent()
        return jdbcTemplate.query(sql, params) { rs, _ ->
            UUID.fromString(rs.getString("facility_id")) to rs.getString("category_code")
        }
    }

    fun fetchPriceInfo(ids: List<UUID>): List<PriceInfoRow> {
        if (ids.isEmpty()) return emptyList()
        val params = MapSqlParameterSource("ids", ids)
        val sql = """
            select facility_id,
                   kind,
                   amount_from,
                   amount_to,
                   currency,
                   note
            from price_info
            where facility_id in (:ids)
        """.trimIndent()
        return jdbcTemplate.query(sql, params) { rs, _ ->
            PriceInfoRow(
                facilityId = UUID.fromString(rs.getString("facility_id")),
                kind = rs.getString("kind"),
                amountFrom = rs.getBigDecimal("amount_from"),
                amountTo = rs.getBigDecimal("amount_to"),
                currency = rs.getString("currency"),
                note = rs.getString("note")
            )
        }
    }

    fun fetchContacts(facilityId: UUID): List<ContactRow> {
        val params = MapSqlParameterSource("facilityId", facilityId)
        val sql = """
            select type, value, label, is_primary
            from contact_point
            where facility_id = :facilityId
            order by is_primary desc, id
        """.trimIndent()
        return jdbcTemplate.query(sql, params) { rs, _ ->
            ContactRow(
                type = rs.getString("type"),
                value = rs.getString("value"),
                label = rs.getString("label"),
                isPrimary = rs.getBoolean("is_primary")
            )
        }
    }

    fun fetchOpeningHours(facilityId: UUID): List<OpeningHoursRow> {
        val params = MapSqlParameterSource("facilityId", facilityId)
        val sql = """
            select day_of_week, is_closed, open_time, close_time, note
            from opening_hours
            where facility_id = :facilityId
            order by day_of_week
        """.trimIndent()
        return jdbcTemplate.query(sql, params) { rs, _ ->
            OpeningHoursRow(
                dayOfWeek = rs.getInt("day_of_week"),
                isClosed = rs.getBoolean("is_closed"),
                openTime = rs.getString("open_time"),
                closeTime = rs.getString("close_time"),
                note = rs.getString("note")
            )
        }
    }

    fun fetchGallery(facilityId: UUID): List<MediaRow> {
        val params = MapSqlParameterSource("facilityId", facilityId)
        val sql = """
            select m.url, fm.caption, fm.sort_order
            from facility_media fm
            join media m on m.id = fm.media_id
            where fm.facility_id = :facilityId
            order by fm.sort_order, m.created_at
        """.trimIndent()
        return jdbcTemplate.query(sql, params) { rs, _ ->
            MediaRow(
                url = rs.getString("url"),
                caption = rs.getString("caption"),
                sortOrder = rs.getInt("sort_order")
            )
        }
    }

    fun fetchCapabilityDetails(facilityId: UUID): List<CapabilityDetailsRow> {
        val params = MapSqlParameterSource("facilityId", facilityId)
        val sql = """
            select ct.code,
                   ct.label,
                   fc.summary,
                   fc.details_json
            from facility_capability fc
            join capability_type ct on ct.id = fc.capability_type_id
            where fc.facility_id = :facilityId
              and fc.is_active = true
        """.trimIndent()
        return jdbcTemplate.query(sql, params) { rs, _ ->
            CapabilityDetailsRow(
                code = rs.getString("code"),
                label = rs.getString("label"),
                summary = rs.getString("summary"),
                details = parseJson(rs, "details_json")
            )
        }
    }

    fun fetchSpaces(facilityId: UUID): List<SpaceRow> {
        val params = MapSqlParameterSource("facilityId", facilityId)
        val sql = """
            select s.id,
                   s.name,
                   st.code as type_code,
                   s.capacity_people,
                   s.size_m2,
                   s.description
            from space s
            join space_type st on st.id = s.space_type_id
            where s.facility_id = :facilityId
            order by s.name
        """.trimIndent()
        return jdbcTemplate.query(sql, params) { rs, _ ->
            SpaceRow(
                id = UUID.fromString(rs.getString("id")),
                name = rs.getString("name"),
                typeCode = rs.getString("type_code"),
                capacityPeople = rs.getInt("capacity_people").takeIf { !rs.wasNull() },
                sizeM2 = rs.getBigDecimal("size_m2"),
                description = rs.getString("description")
            )
        }
    }

    fun fetchSpaceMedia(spaceIds: List<UUID>): List<SpaceMediaRow> {
        if (spaceIds.isEmpty()) return emptyList()
        val params = MapSqlParameterSource("spaceIds", spaceIds)
        val sql = """
            select sm.space_id,
                   m.url,
                   sm.caption,
                   sm.sort_order
            from space_media sm
            join media m on m.id = sm.media_id
            where sm.space_id in (:spaceIds)
            order by sm.sort_order, m.created_at
        """.trimIndent()
        return jdbcTemplate.query(sql, params) { rs, _ ->
            SpaceMediaRow(
                spaceId = UUID.fromString(rs.getString("space_id")),
                url = rs.getString("url"),
                caption = rs.getString("caption"),
                sortOrder = rs.getInt("sort_order")
            )
        }
    }

    fun fetchEquipment(facilityId: UUID): List<EquipmentRow> {
        val params = MapSqlParameterSource("facilityId", facilityId)
        val sql = """
            select et.category_code,
                   et.name,
                   fe.quantity,
                   fe.mode,
                   fe.note
            from facility_equipment fe
            join equipment_type et on et.id = fe.equipment_type_id
            where fe.facility_id = :facilityId
            order by et.category_code, et.name
        """.trimIndent()
        return jdbcTemplate.query(sql, params) { rs, _ ->
            EquipmentRow(
                facilityId = facilityId,
                categoryCode = rs.getString("category_code"),
                name = rs.getString("name"),
                quantity = rs.getInt("quantity").takeIf { !rs.wasNull() },
                mode = rs.getString("mode"),
                note = rs.getString("note")
            )
        }
    }

    fun fetchFeatures(facilityId: UUID): List<FeatureRow> {
        val params = MapSqlParameterSource("facilityId", facilityId)
        val sql = """
            select fe.code,
                   fe.label,
                   ff.value_bool,
                   ff.value_text,
                   ff.value_number
            from facility_feature ff
            join feature fe on fe.id = ff.feature_id
            where ff.facility_id = :facilityId
            order by fe.code
        """.trimIndent()
        return jdbcTemplate.query(sql, params) { rs, _ ->
            FeatureRow(
                facilityId = facilityId,
                code = rs.getString("code"),
                label = rs.getString("label"),
                valueBool = rs.getObject("value_bool", Boolean::class.javaObjectType),
                valueText = rs.getString("value_text"),
                valueNumber = rs.getBigDecimal("value_number")
            )
        }
    }

    fun fetchFacilityBase(facilityId: UUID): FacilityBaseRow? {
        val params = MapSqlParameterSource("facilityId", facilityId)
        val sql = """
            select f.id,
                   f.name,
                   f.description,
                   c.id as city_id,
                   c.name as city_name,
                   a.label as address_label,
                   a.lat,
                   a.lng,
                   m.url as cover_image_url
            from facility f
            join city c on c.id = f.city_id
            left join address a on a.id = f.address_id
            left join media m on m.id = f.cover_media_id
            where f.id = :facilityId
              and f.status = 'ACTIVE'
        """.trimIndent()
        return jdbcTemplate.query(sql, params) { rs, _ -> mapFacilityBaseRow(rs) }.firstOrNull()
    }

    private fun mapFacilityBaseRow(rs: ResultSet): FacilityBaseRow = FacilityBaseRow(
        id = UUID.fromString(rs.getString("id")),
        name = rs.getString("name"),
        description = rs.getString("description"),
        cityId = UUID.fromString(rs.getString("city_id")),
        cityName = rs.getString("city_name"),
        addressLabel = rs.getString("address_label"),
        lat = rs.getBigDecimal("lat"),
        lng = rs.getBigDecimal("lng"),
        coverImageUrl = rs.getString("cover_image_url")
    )

    private fun parseJson(rs: ResultSet, column: String): JsonNode? {
        val value = rs.getObject(column) ?: return null
        val jsonText = value.toString()
        return objectMapper.readTree(jsonText)
    }
}
