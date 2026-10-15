package com.petadopt.dao;

import com.petadopt.exception.DataAccessException;
import com.petadopt.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Shared JDBC plumbing. Every statement is a {@link PreparedStatement} (no string-built SQL values)
 * and every resource is closed with try-with-resources.
 * Overloads that take a {@link Connection} join an existing transaction and never close it.
 */
abstract class BaseDAO {

    @FunctionalInterface
    protected interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    protected Connection open() throws SQLException {
        return DBConnection.getInstance().getConnection();
    }

    private static void bind(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object value = params[i];
            if (value instanceof Enum) {
                value = ((Enum<?>) value).name();
            }
            ps.setObject(i + 1, value);
        }
    }

    // ---------------------------------------------------------------- queries
    protected <T> List<T> queryList(Connection c, String sql, RowMapper<T> mapper, Object... params)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> rows = new ArrayList<>();
                while (rs.next()) {
                    rows.add(mapper.map(rs));
                }
                return rows;
            }
        }
    }

    protected <T> List<T> queryList(String sql, RowMapper<T> mapper, Object... params) {
        try (Connection c = open()) {
            return queryList(c, sql, mapper, params);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    protected <T> Optional<T> queryOne(Connection c, String sql, RowMapper<T> mapper, Object... params)
            throws SQLException {
        List<T> rows = queryList(c, sql, mapper, params);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    protected <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... params) {
        List<T> rows = queryList(sql, mapper, params);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    protected long count(String sql, Object... params) {
        return queryOne(sql, rs -> rs.getLong(1), params).orElse(0L);
    }

    // ---------------------------------------------------------------- writes
    protected int update(Connection c, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        }
    }

    protected int update(String sql, Object... params) {
        try (Connection c = open()) {
            return update(c, sql, params);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /** Runs an INSERT and returns the generated primary key. */
    protected long insert(String sql, Object... params) {
        try (Connection c = open();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, params);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : 0L;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
