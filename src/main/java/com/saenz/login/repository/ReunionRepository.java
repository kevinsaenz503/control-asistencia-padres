package com.saenz.login.repository;

import com.saenz.login.model.Reunion;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class ReunionRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReunionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Reunion> findAll() {
        String sql = """
            SELECT id_reunion, fecha, tipo_reunion
            FROM reuniones
            ORDER BY fecha DESC, id_reunion DESC
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs));
    }

    public Optional<Reunion> findById(Long id) {
        String sql = """
            SELECT id_reunion, fecha, tipo_reunion
            FROM reuniones
            WHERE id_reunion = ?
            """;
        List<Reunion> result = jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs), id);
        return result.stream().findFirst();
    }

    public void save(Reunion reunion) {
        try {
            if (reunion.getIdReunion() == null || reunion.getIdReunion() <= 0) {
                jdbcTemplate.update("""
                    INSERT INTO reuniones (fecha, tipo_reunion)
                    VALUES (?, ?)
                    """,
                    reunion.getFecha() == null ? null : Date.valueOf(reunion.getFecha()),
                    clean(reunion.getTipoReunion()));
            } else {
                jdbcTemplate.update("""
                    UPDATE reuniones
                    SET fecha = ?, tipo_reunion = ?
                    WHERE id_reunion = ?
                    """,
                    reunion.getFecha() == null ? null : Date.valueOf(reunion.getFecha()),
                    clean(reunion.getTipoReunion()), reunion.getIdReunion());
            }
        } catch (DataAccessException ex) {
            throw new DatabaseConnectionException("No se pudo guardar la reunión.", ex);
        }
    }

    public void deleteById(Long id) {
        try {
            jdbcTemplate.update("DELETE FROM reuniones WHERE id_reunion = ?", id);
        } catch (DataAccessException ex) {
            throw new DatabaseConnectionException(
                    "No se pudo eliminar la reunión. Si tiene asistencias asociadas, elimínelas primero.", ex);
        }
    }

    private Reunion mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        Reunion r = new Reunion();
        r.setIdReunion(rs.getLong("id_reunion"));
        if (rs.getDate("fecha") != null) {
            r.setFecha(rs.getDate("fecha").toLocalDate());
        }
        r.setTipoReunion(rs.getString("tipo_reunion"));
        return r;
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
