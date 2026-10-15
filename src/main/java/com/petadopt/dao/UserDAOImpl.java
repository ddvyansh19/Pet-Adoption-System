package com.petadopt.dao;

import com.petadopt.exception.DataAccessException;
import com.petadopt.exception.DuplicateEmailException;
import com.petadopt.model.Role;
import com.petadopt.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl extends BaseDAO implements UserDAO {
    private static final String SELECT =
            "SELECT id, name, email, password_hash, role, phone, address, created_at FROM users ";

    private static User map(ResultSet rs) throws SQLException {
        User u = User.create(Role.valueOf(rs.getString("role")));
        u.setId(rs.getLong("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setPhone(rs.getString("phone"));
        u.setAddress(rs.getString("address"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }

    @Override
    public long create(User u) {
        try {
            return insert("INSERT INTO users (name, email, password_hash, role, phone, address) VALUES (?,?,?,?,?,?)",
                    u.getName(), u.getEmail(), u.getPasswordHash(), u.getRole(), blankToNull(u.getPhone()),
                    blankToNull(u.getAddress()));
        } catch (DataAccessException e) {
            if (e.isDuplicateKey()) {
                throw new DuplicateEmailException(u.getEmail());
            }
            throw e;
        }
    }

    @Override
    public Optional<User> findById(long id) {
        return queryOne(SELECT + "WHERE id = ?", UserDAOImpl::map, id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return queryOne(SELECT + "WHERE email = ?", UserDAOImpl::map, email);
    }

    @Override
    public List<User> findAll(Role role) {
        if (role == null) {
            return queryList(SELECT + "ORDER BY created_at DESC, id DESC", UserDAOImpl::map);
        }
        return queryList(SELECT + "WHERE role = ? ORDER BY created_at DESC, id DESC", UserDAOImpl::map, role);
    }

    @Override
    public void update(User u) {
        try {
            update("UPDATE users SET name = ?, email = ?, role = ?, phone = ?, address = ? WHERE id = ?",
                    u.getName(), u.getEmail(), u.getRole(), blankToNull(u.getPhone()), blankToNull(u.getAddress()),
                    u.getId());
        } catch (DataAccessException e) {
            if (e.isDuplicateKey()) {
                throw new DuplicateEmailException(u.getEmail());
            }
            throw e;
        }
    }

    @Override
    public void updateProfile(long id, String name, String phone, String address) {
        update("UPDATE users SET name = ?, phone = ?, address = ? WHERE id = ?", name, blankToNull(phone),
                blankToNull(address), id);
    }

    @Override
    public void updatePassword(long id, String passwordHash) {
        update("UPDATE users SET password_hash = ? WHERE id = ?", passwordHash, id);
    }

    @Override
    public boolean delete(long id) {
        return update("DELETE FROM users WHERE id = ?", id) > 0;
    }

    @Override
    public boolean emailExists(String email, long excludeId) {
        return count("SELECT COUNT(*) FROM users WHERE email = ? AND id <> ?", email, excludeId) > 0;
    }

    @Override
    public List<User> findAdoptersForShelter(long shelterId) {
        return queryList("SELECT DISTINCT u.id, u.name, u.email, u.password_hash, u.role, u.phone, u.address, "
                + "u.created_at FROM users u JOIN applications a ON a.adopter_id = u.id "
                + "JOIN pets p ON p.id = a.pet_id WHERE p.shelter_id = ? ORDER BY u.name", UserDAOImpl::map,
                shelterId);
    }

    @Override
    public List<User> findSheltersForAdopter(long adopterId) {
        return queryList("SELECT DISTINCT u.id, u.name, u.email, u.password_hash, u.role, u.phone, u.address, "
                + "u.created_at FROM users u JOIN pets p ON p.shelter_id = u.id "
                + "JOIN applications a ON a.pet_id = p.id WHERE a.adopter_id = ? ORDER BY u.name", UserDAOImpl::map,
                adopterId);
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
