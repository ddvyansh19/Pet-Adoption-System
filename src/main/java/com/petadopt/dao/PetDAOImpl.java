package com.petadopt.dao;

import com.petadopt.model.AdoptionStatus;
import com.petadopt.model.ApprovalStatus;
import com.petadopt.model.Pet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PetDAOImpl extends BaseDAO implements PetDAO {
    private static final String SELECT =
            "SELECT p.*, u.name AS shelter_name FROM pets p JOIN users u ON u.id = p.shelter_id ";

    private static Pet map(ResultSet rs) throws SQLException {
        Pet p = new Pet();
        p.setId(rs.getLong("id"));
        p.setShelterId(rs.getLong("shelter_id"));
        p.setShelterName(rs.getString("shelter_name"));
        p.setName(rs.getString("name"));
        p.setType(rs.getString("type"));
        p.setBreed(rs.getString("breed"));
        p.setAgeYears(rs.getDouble("age_years"));
        p.setLocation(rs.getString("location"));
        p.setDescription(rs.getString("description"));
        p.setPhotoPath(rs.getString("photo_path"));
        p.setAdoptionStatus(AdoptionStatus.valueOf(rs.getString("adoption_status")));
        p.setApprovalStatus(ApprovalStatus.valueOf(rs.getString("approval_status")));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }

    @Override
    public long create(Pet p) {
        return insert("INSERT INTO pets (shelter_id, name, type, breed, age_years, location, description, "
                + "photo_path, adoption_status, approval_status) VALUES (?,?,?,?,?,?,?,?,?,?)",
                p.getShelterId(), p.getName(), p.getType(), p.getBreed(), p.getAgeYears(), p.getLocation(),
                p.getDescription(), p.getPhotoPath(), p.getAdoptionStatus(), p.getApprovalStatus());
    }

    @Override
    public Optional<Pet> findById(long id) {
        return queryOne(SELECT + "WHERE p.id = ?", PetDAOImpl::map, id);
    }

    @Override
    public void update(Pet p) {
        update("UPDATE pets SET name = ?, type = ?, breed = ?, age_years = ?, location = ?, description = ?, "
                + "photo_path = ?, adoption_status = ?, approval_status = ? WHERE id = ? AND shelter_id = ?",
                p.getName(), p.getType(), p.getBreed(), p.getAgeYears(), p.getLocation(), p.getDescription(),
                p.getPhotoPath(), p.getAdoptionStatus(), p.getApprovalStatus(), p.getId(), p.getShelterId());
    }

    @Override
    public void setApproval(long petId, ApprovalStatus status) {
        update("UPDATE pets SET approval_status = ? WHERE id = ?", status, petId);
    }

    @Override
    public boolean delete(long petId, long shelterId) {
        return update("DELETE FROM pets WHERE id = ? AND shelter_id = ?", petId, shelterId) > 0;
    }

    @Override
    public List<Pet> findByShelter(long shelterId) {
        return queryList(SELECT + "WHERE p.shelter_id = ? ORDER BY p.created_at DESC, p.id DESC",
                PetDAOImpl::map, shelterId);
    }

    @Override
    public List<Pet> findAll(ApprovalStatus approval) {
        if (approval == null) {
            return queryList(SELECT + "ORDER BY p.created_at DESC, p.id DESC", PetDAOImpl::map);
        }
        return queryList(SELECT + "WHERE p.approval_status = ? ORDER BY p.created_at DESC, p.id DESC",
                PetDAOImpl::map, approval);
    }

    @Override
    public List<Pet> search(String type, String breed, String location) {
        StringBuilder sql = new StringBuilder(SELECT)
                .append("WHERE p.approval_status = 'APPROVED' AND p.adoption_status = 'AVAILABLE'");
        List<Object> params = new ArrayList<>();
        if (type != null && !type.isBlank()) {
            sql.append(" AND p.type = ?");
            params.add(type.trim());
        }
        if (breed != null && !breed.isBlank()) {
            sql.append(" AND p.breed LIKE ?");
            params.add("%" + breed.trim() + "%");
        }
        if (location != null && !location.isBlank()) {
            sql.append(" AND p.location LIKE ?");
            params.add("%" + location.trim() + "%");
        }
        sql.append(" ORDER BY p.created_at DESC, p.id DESC");
        return queryList(sql.toString(), PetDAOImpl::map, params.toArray());
    }

    @Override
    public List<String> findDistinctTypes() {
        return queryList("SELECT DISTINCT type FROM pets WHERE approval_status = 'APPROVED' ORDER BY type",
                rs -> rs.getString(1));
    }

    @Override
    public int markAdopted(Connection tx, long petId) throws SQLException {
        return update(tx, "UPDATE pets SET adoption_status = 'ADOPTED' WHERE id = ? AND adoption_status = 'AVAILABLE'",
                petId);
    }

    @Override
    public AdoptionStatus statusOf(long petId) {
        return queryOne("SELECT adoption_status FROM pets WHERE id = ?",
                rs -> AdoptionStatus.valueOf(rs.getString(1)), petId).orElse(null);
    }
}
