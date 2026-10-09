package com.saenz.login.repository;

import com.saenz.login.model.Asistencia;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

@Repository
public class AsistenciaRepository {

    private final JdbcTemplate jdbcTemplate;

    public AsistenciaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // CONSULTA BASE
    // =========================================================

    private static final String BASE_SELECT = """
        SELECT
            a.id_asistencia,
            a.id_padre,
            a.id_reunion,
            a.estado,
            a.hora_llegada,
            a.firma_digital,

            p.nombre_completo AS nombre_padre,
            p.dui,

            r.fecha AS fecha_reunion,
            r.tipo_reunion

        FROM asistencia a

        LEFT JOIN padres p
            ON p.id_padre = a.id_padre

        LEFT JOIN reuniones r
            ON r.id_reunion = a.id_reunion
        """;


    // =========================================================
    // MAPEAR RESULTADO
    // =========================================================

    private Asistencia mapRow(
            ResultSet rs,
            int rowNum) throws SQLException {

        Asistencia a = new Asistencia();

        a.setIdAsistencia(
                rs.getLong("id_asistencia")
        );

        a.setIdPadre(
                rs.getLong("id_padre")
        );

        a.setIdReunion(
                rs.getLong("id_reunion")
        );

        a.setEstado(
                rs.getString("estado")
        );

        Time hora =
                rs.getTime("hora_llegada");

        if (hora != null) {

            a.setHoraLlegada(
                    hora.toLocalTime()
            );

        } else {

            a.setHoraLlegada(null);

        }

        a.setFirmaDigital(
                rs.getString("firma_digital")
        );

        a.setNombrePadre(
                rs.getString("nombre_padre")
        );

        a.setDui(
                rs.getString("dui")
        );

        java.sql.Date fecha =
                rs.getDate("fecha_reunion");

        if (fecha != null) {

            a.setFechaReunion(
                    fecha.toLocalDate()
            );

        } else {

            a.setFechaReunion(null);

        }

        a.setTipoReunion(
                rs.getString("tipo_reunion")
        );

        return a;
    }


    // =========================================================
    // BUSCAR TODAS
    // =========================================================

    public List<Asistencia> findAll() {

        String sql = BASE_SELECT + """
            ORDER BY
                r.fecha DESC NULLS LAST,
                a.id_asistencia DESC
            """;

        return jdbcTemplate.query(
                sql,
                this::mapRow
        );
    }


    // =========================================================
    // BUSCAR UNA POR ID
    // =========================================================

    public Optional<Asistencia> findById(Long id) {

        if (id == null) {
            return Optional.empty();
        }

        String sql = BASE_SELECT + """
            WHERE a.id_asistencia = ?
            """;

        List<Asistencia> resultado =
                jdbcTemplate.query(
                        sql,
                        this::mapRow,
                        id
                );

        return resultado.stream().findFirst();
    }


    // =========================================================
    // BUSCAR ASISTENCIAS DE HOY
    // =========================================================

    public List<Asistencia> findToday() {

        String sql = BASE_SELECT + """
            WHERE r.fecha::date = CURRENT_DATE

            ORDER BY
                a.id_asistencia DESC
            """;

        return jdbcTemplate.query(
                sql,
                this::mapRow
        );
    }


    // =========================================================
    // CONTAR TODAS
    // =========================================================

    public int countAll() {

        Integer resultado =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM asistencia
                        """,
                        Integer.class
                );

        return resultado == null
                ? 0
                : resultado;
    }


    // =========================================================
    // CONTAR PRESENTES
    // =========================================================

    public int countPresent() {

        Integer resultado =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM asistencia
                        WHERE LOWER(TRIM(estado))
                        IN (
                            'asistió',
                            'asistio',
                            'presente',
                            'p',
                            'si',
                            'sí'
                        )
                        """,
                        Integer.class
                );

        return resultado == null
                ? 0
                : resultado;
    }


    // =========================================================
    // CONTAR AUSENTES
    // =========================================================

    public int countAbsent() {

        Integer resultado =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM asistencia
                        WHERE LOWER(TRIM(estado))
                        IN (
                            'no asistió',
                            'no asistio',
                            'ausente',
                            'a',
                            'no'
                        )
                        """,
                        Integer.class
                );

        return resultado == null
                ? 0
                : resultado;
    }


    // =========================================================
    // CONTAR ASISTENCIAS DE HOY
    // =========================================================

    public int countToday() {

        Integer resultado =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM asistencia a

                        INNER JOIN reuniones r
                            ON r.id_reunion = a.id_reunion

                        WHERE r.fecha::date = CURRENT_DATE
                        """,
                        Integer.class
                );

        return resultado == null
                ? 0
                : resultado;
    }


    // =========================================================
    // CONTAR PRESENTES DE HOY
    // =========================================================

    public int countPresentToday() {

        Integer resultado =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM asistencia a

                        INNER JOIN reuniones r
                            ON r.id_reunion = a.id_reunion

                        WHERE r.fecha::date = CURRENT_DATE

                        AND LOWER(TRIM(a.estado))
                        IN (
                            'asistió',
                            'asistio',
                            'presente',
                            'p',
                            'si',
                            'sí'
                        )
                        """,
                        Integer.class
                );

        return resultado == null
                ? 0
                : resultado;
    }


    // =========================================================
    // CONTAR AUSENTES DE HOY
    // =========================================================

    public int countAbsentToday() {

        Integer resultado =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM asistencia a

                        INNER JOIN reuniones r
                            ON r.id_reunion = a.id_reunion

                        WHERE r.fecha::date = CURRENT_DATE

                        AND LOWER(TRIM(a.estado))
                        IN (
                            'no asistió',
                            'no asistio',
                            'ausente',
                            'a',
                            'no'
                        )
                        """,
                        Integer.class
                );

        return resultado == null
                ? 0
                : resultado;
    }


    // =========================================================
    // GUARDAR
    // =========================================================

    public boolean save(Asistencia a) {

        if (a == null) {
            return false;
        }


        // -----------------------------------------------------
        // ACTUALIZAR
        // -----------------------------------------------------

        if (a.getIdAsistencia() != null
                && a.getIdAsistencia() > 0) {

            int filas =
                    jdbcTemplate.update(
                            """
                            UPDATE asistencia

                            SET
                                id_padre = ?,
                                id_reunion = ?,
                                estado = ?,
                                hora_llegada = ?,
                                firma_digital = ?

                            WHERE id_asistencia = ?
                            """,

                            a.getIdPadre(),

                            a.getIdReunion(),

                            a.getEstado(),

                            a.getHoraLlegada() != null
                                    ? Time.valueOf(
                                        a.getHoraLlegada()
                                      )
                                    : null,

                            a.getFirmaDigital(),

                            a.getIdAsistencia()
                    );

            return filas > 0;
        }


        // -----------------------------------------------------
        // INSERTAR
        // -----------------------------------------------------

        int filas =
                jdbcTemplate.update(
                        """
                        INSERT INTO asistencia
                        (
                            id_padre,
                            id_reunion,
                            estado,
                            hora_llegada,
                            firma_digital
                        )

                        VALUES
                        (?, ?, ?, ?, ?)
                        """,

                        a.getIdPadre(),

                        a.getIdReunion(),

                        a.getEstado(),

                        a.getHoraLlegada() != null
                                ? Time.valueOf(
                                    a.getHoraLlegada()
                                  )
                                : null,

                        a.getFirmaDigital()
                );

        return filas > 0;
    }


    // =========================================================
    // ELIMINAR
    // =========================================================

    public boolean deleteById(Long id) {

        if (id == null) {
            return false;
        }

        int filas =
                jdbcTemplate.update(
                        """
                        DELETE FROM asistencia
                        WHERE id_asistencia = ?
                        """,
                        id
                );

        return filas > 0;
    }


    // =========================================================
    // MÉTODO COMPATIBLE CON EL FORMULARIO ANTERIOR
    // =========================================================

    public boolean guardarAsistencia(
            Long idPadre,
            Long idReunion,
            String estado,
            String horaLlegada,
            String firmaDigital) {

        Time hora = null;

        if (horaLlegada != null
                && !horaLlegada.isBlank()) {

            hora = Time.valueOf(horaLlegada);
        }

        int filas =
                jdbcTemplate.update(
                        """
                        INSERT INTO asistencia
                        (
                            id_padre,
                            id_reunion,
                            estado,
                            hora_llegada,
                            firma_digital
                        )

                        VALUES
                        (?, ?, ?, ?, ?)
                        """,

                        idPadre,
                        idReunion,
                        estado,
                        hora,
                        firmaDigital
                );

        return filas > 0;
    }
}