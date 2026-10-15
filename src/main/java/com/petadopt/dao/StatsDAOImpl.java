package com.petadopt.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatsDAOImpl extends BaseDAO implements StatsDAO {

    /** Maps a single-row result into {columnLabel -> long value}. */
    private static Map<String, Long> row(ResultSet rs) throws SQLException {
        Map<String, Long> map = new LinkedHashMap<>();
        ResultSetMetaData meta = rs.getMetaData();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            map.put(meta.getColumnLabel(i), rs.getLong(i));
        }
        return map;
    }

    @Override
    public Map<String, Long> platformCounts() {
        return queryOne("SELECT "
                + "(SELECT COUNT(*) FROM users) AS total_users, "
                + "(SELECT COUNT(*) FROM users WHERE role = 'SHELTER') AS total_shelters, "
                + "(SELECT COUNT(*) FROM users WHERE role = 'ADOPTER') AS total_adopters, "
                + "(SELECT COUNT(*) FROM pets) AS total_pets, "
                + "(SELECT COUNT(*) FROM pets WHERE approval_status = 'PENDING') AS pending_pets, "
                + "(SELECT COUNT(*) FROM pets WHERE approval_status = 'APPROVED' AND adoption_status = 'AVAILABLE') "
                + "AS available_pets, "
                + "(SELECT COUNT(*) FROM pets WHERE adoption_status = 'ADOPTED') AS adopted_pets, "
                + "(SELECT COUNT(*) FROM applications) AS total_applications, "
                + "(SELECT COUNT(*) FROM applications WHERE status = 'PENDING') AS pending_applications",
                StatsDAOImpl::row).orElse(Collections.emptyMap());
    }

    @Override
    public Map<String, Long> petsByType() {
        Map<String, Long> result = new LinkedHashMap<>();
        queryList("SELECT type, COUNT(*) AS total FROM pets WHERE approval_status = 'APPROVED' "
                + "GROUP BY type ORDER BY total DESC, type",
                rs -> result.put(rs.getString("type"), rs.getLong("total")));
        return result;
    }

    @Override
    public List<Map<String, Object>> topShelters(int limit) {
        return queryList("SELECT u.name AS name, COUNT(p.id) AS adopted FROM users u "
                + "LEFT JOIN pets p ON p.shelter_id = u.id AND p.adoption_status = 'ADOPTED' "
                + "WHERE u.role = 'SHELTER' GROUP BY u.id, u.name ORDER BY adopted DESC, u.name LIMIT ?", rs -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("name", rs.getString("name"));
                    m.put("adopted", rs.getLong("adopted"));
                    return m;
                }, limit);
    }

    @Override
    public Map<String, Long> shelterCounts(long shelterId) {
        return queryOne("SELECT "
                + "(SELECT COUNT(*) FROM pets WHERE shelter_id = ?) AS total_pets, "
                + "(SELECT COUNT(*) FROM pets WHERE shelter_id = ? AND approval_status = 'APPROVED' "
                + "AND adoption_status = 'AVAILABLE') AS available_pets, "
                + "(SELECT COUNT(*) FROM pets WHERE shelter_id = ? AND approval_status = 'PENDING') AS awaiting_approval, "
                + "(SELECT COUNT(*) FROM pets WHERE shelter_id = ? AND adoption_status = 'ADOPTED') AS adopted_pets, "
                + "(SELECT COUNT(*) FROM applications a JOIN pets p ON p.id = a.pet_id WHERE p.shelter_id = ?) "
                + "AS total_applications, "
                + "(SELECT COUNT(*) FROM applications a JOIN pets p ON p.id = a.pet_id WHERE p.shelter_id = ? "
                + "AND a.status = 'PENDING') AS pending_applications, "
                + "(SELECT COUNT(*) FROM applications a JOIN pets p ON p.id = a.pet_id WHERE p.shelter_id = ? "
                + "AND a.status = 'APPROVED') AS approved_applications, "
                + "(SELECT COUNT(*) FROM applications a JOIN pets p ON p.id = a.pet_id WHERE p.shelter_id = ? "
                + "AND a.status = 'REJECTED') AS rejected_applications",
                StatsDAOImpl::row, shelterId, shelterId, shelterId, shelterId, shelterId, shelterId, shelterId,
                shelterId).orElse(Collections.emptyMap());
    }

    @Override
    public Map<String, Long> adopterCounts(long adopterId) {
        return queryOne("SELECT "
                + "(SELECT COUNT(*) FROM applications WHERE adopter_id = ?) AS total_applications, "
                + "(SELECT COUNT(*) FROM applications WHERE adopter_id = ? AND status = 'PENDING') AS pending_applications, "
                + "(SELECT COUNT(*) FROM applications WHERE adopter_id = ? AND status = 'APPROVED') AS approved_applications, "
                + "(SELECT COUNT(*) FROM applications WHERE adopter_id = ? AND status = 'REJECTED') AS rejected_applications",
                StatsDAOImpl::row, adopterId, adopterId, adopterId, adopterId).orElse(Collections.emptyMap());
    }
}
