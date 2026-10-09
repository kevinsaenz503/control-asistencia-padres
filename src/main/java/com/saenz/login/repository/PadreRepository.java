package com.saenz.login.repository;

import com.saenz.login.model.Padre;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PadreRepository {

    private final JdbcTemplate jdbcTemplate;

    public PadreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Padre> findAll() {
        String sql = """
            SELECT id_padre, nombre_completo, dui, telefono,
                   nombre_estudiante, grado_seccion
            FROM padres
            ORDER BY nombre_completo
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs));
    }

    public Optional<Padre> findById(Long id) {
        String sql = """
            SELECT id_padre, nombre_completo, dui, telefono,
                   nombre_estudiante, grado_seccion
            FROM padres
            WHERE id_padre = ?
            """;
        List<Padre> result = jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs), id);
        return result.stream().findFirst();
    }

    public int count() {
        Integer result = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM padres", Integer.class);
        return result == null ? 0 : result;
    }

    public void save(Padre padre) {
        try {
            if (padre.getIdPadre() == null || padre.getIdPadre() <= 0) {
                jdbcTemplate.update("""
                    INSERT INTO padres
                    (nombre_completo, dui, telefono, nombre_estudiante, grado_seccion)
                    VALUES (?, ?, ?, ?, ?)
                    """,
                    clean(padre.getNombreCompleto()), clean(padre.getDui()), clean(padre.getTelefono()),
                    clean(padre.getNombreEstudiante()), clean(padre.getGradoSeccion()));
            } else {
                jdbcTemplate.update("""
                    UPDATE padres
                    SET nombre_completo = ?, dui = ?, telefono = ?,
                        nombre_estudiante = ?, grado_seccion = ?
                    WHERE id_padre = ?
                    """,
                    clean(padre.getNombreCompleto()), clean(padre.getDui()), clean(padre.getTelefono()),
                    clean(padre.getNombreEstudiante()), clean(padre.getGradoSeccion()), padre.getIdPadre());
            }
        } catch (DataAccessException ex) {
            throw new DatabaseConnectionException("No se pudo guardar el padre.", ex);
        }
    }

    public void deleteById(Long id) {
        try {
            jdbcTemplate.update("DELETE FROM padres WHERE id_padre = ?", id);
        } catch (DataAccessException ex) {
            throw new DatabaseConnectionException(
                    "No se pudo eliminar el padre. Si tiene asistencias asociadas, elimínelas primero.", ex);
        }
    }

    private Padre mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        Padre p = new Padre();
        p.setIdPadre(rs.getLong("id_padre"));
        p.setNombreCompleto(rs.getString("nombre_completo"));
        p.setDui(rs.getString("dui"));
        p.setTelefono(rs.getString("telefono"));
        p.setNombreEstudiante(rs.getString("nombre_estudiante"));
        p.setGradoSeccion(rs.getString("grado_seccion"));
        return p;
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
