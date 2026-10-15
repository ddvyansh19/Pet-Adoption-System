package com.petadopt.dao;

import com.petadopt.model.Application;
import com.petadopt.model.ApplicationStatus;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ApplicationDAOImpl extends BaseDAO implements ApplicationDAO {
    private static final String SELECT = "SELECT a.*, p.name AS pet_name, p.photo_path AS pet_photo, "
            + "p.shelter_id AS shelter_id, s.name AS shelter_name, ad.name AS adopter_name, ad.email AS adopter_email "
            + "FROM applications a JOIN pets p ON p.id = a.pet_id JOIN users s ON s.id = p.shelter_id "
            + "JOIN users ad ON ad.id = a.adopter_id ";

    private static Application map(ResultSet rs) throws SQLException {
        Application a = new Application();
        a.setId(rs.getLong("id"));
        a.setPetId(rs.getLong("pet_id"));
        a.setPetName(rs.getString("pet_name"));
        a.setPetPhoto(rs.getString("pet_photo"));
        a.setShelterId(rs.getLong("shelter_id"));
        a.setShelterName(rs.getString("shelter_name"));
        a.setAdopterId(rs.getLong("adopter_id"));
        a.setAdopterName(rs.getString("adopter_name"));
        a.setAdopterEmail(rs.getString("adopter_email"));
        a.setReason(rs.getString("reason"));
        a.setHousingType(rs.getString("housing_type"));
        a.setHasOtherPets(rs.getBoolean("has_other_pets"));
        a.setExperience(rs.getString("experience"));
        a.setStatus(ApplicationStatus.valueOf(rs.getString("status")));
        a.setShelterNote(rs.getString("shelter_note"));
        a.setAppliedAt(rs.getTimestamp("applied_at"));
        a.setUpdatedAt(rs.getTimestamp("updated_at"));
        return a;
    }

    @Override
    public long create(Application a) {
        return insert("INSERT INTO applications (pet_id, adopter_id, reason, housing_type, has_other_pets, "
                + "experience) VALUES (?,?,?,?,?,?)",
                a.getPetId(), a.getAdopterId(), a.getReason(), a.getHousingType(), a.isHasOtherPets(),
                a.getExperience());
    }

    @Override
    public Optional<Application> findById(long id) {
        return queryOne(SELECT + "WHERE a.id = ?", ApplicationDAOImpl::map, id);
    }

    @Override
    public List<Application> findByAdopter(long adopterId) {
        return queryList(SELECT + "WHERE a.adopter_id = ? ORDER BY a.applied_at DESC, a.id DESC",
                ApplicationDAOImpl::map, adopterId);
    }

    @Override
    public List<Application> findByShelter(long shelterId, ApplicationStatus status) {
        if (status == null) {
            return queryList(SELECT + "WHERE p.shelter_id = ? ORDER BY a.applied_at DESC, a.id DESC",
                    ApplicationDAOImpl::map, shelterId);
        }
        return queryList(SELECT + "WHERE p.shelter_id = ? AND a.status = ? ORDER BY a.applied_at DESC, a.id DESC",
                ApplicationDAOImpl::map, shelterId, status);
    }

    @Override
    public boolean exists(long petId, long adopterId) {
        return count("SELECT COUNT(*) FROM applications WHERE pet_id = ? AND adopter_id = ?", petId, adopterId) > 0;
    }

    @Override
    public long countPendingByAdopter(long adopterId) {
        return count("SELECT COUNT(*) FROM applications WHERE adopter_id = ? AND status = 'PENDING'", adopterId);
    }

    @Override
    public Optional<Application> findByIdForUpdate(Connection tx, long id) throws SQLException {
        return queryOne(tx, SELECT + "WHERE a.id = ? FOR UPDATE", ApplicationDAOImpl::map, id);
    }

    @Override
    public void updateStatus(Connection tx, long id, ApplicationStatus status, String note) throws SQLException {
        update(tx, "UPDATE applications SET status = ?, shelter_note = ? WHERE id = ?", status,
                (note == null || note.isBlank()) ? null : note.trim(), id);
    }

    @Override
    public int rejectOthers(Connection tx, long petId, long exceptApplicationId, String note) throws SQLException {
        return update(tx, "UPDATE applications SET status = 'REJECTED', shelter_note = ? "
                + "WHERE pet_id = ? AND id <> ? AND status = 'PENDING'", note, petId, exceptApplicationId);
    }
}
