package com.saenz.login.repository;

import com.saenz.login.model.User;
import com.saenz.login.security.PasswordHasher;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // =====================================================
    // LOGIN
    // =====================================================

    public User authenticate(String username, String password) {

        String sql = """
                SELECT id_usuario, usuario, password, ingreso
                FROM usuarios
                WHERE LOWER(TRIM(usuario)) = LOWER(TRIM(?))
                  AND password = ?
                LIMIT 1
                """;

        String encryptedPassword = PasswordHasher.sha256(password);

        try {

            User user = jdbcTemplate.query(sql, rs -> {

                if (!rs.next()) {
                    return null;
                }

                return new User(
                        rs.getLong("id_usuario"),
                        rs.getString("usuario"),
                        rs.getString("password"),
                        rs.getTimestamp("ingreso") == null ? null : rs.getTimestamp("ingreso").toLocalDateTime()
                );

            }, username, encryptedPassword);

            if (user != null) {

                jdbcTemplate.update(
                        """
                        UPDATE usuarios
                        SET ingreso = CURRENT_TIMESTAMP
                        WHERE id_usuario = ?
                        """,
                        user.getId()
                );

                registrarBitacora(
                        user.getId(),
                        user.getId(),
                        "LOGIN",
                        "Inicio de sesión"
                );
            }

            return user;

        } catch (DataAccessException ex) {
            throw new DatabaseConnectionException(
                    "Error al autenticar usuario.",
                    ex
            );
        }
    }

    // =====================================================
    // LISTAR USUARIOS
    // =====================================================

    public List<User> findAll() {

        String sql = """
                SELECT
                    id_usuario,
                    usuario,
                    password,
                    ingreso
                FROM usuarios
                ORDER BY id_usuario
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new User(
                        rs.getLong("id_usuario"),
                        rs.getString("usuario"),
                        rs.getString("password"),
                        rs.getTimestamp("ingreso") == null
                                ? null
                                : rs.getTimestamp("ingreso").toLocalDateTime()
                )
        );
    }

    // =====================================================
    // GUARDAR / MODIFICAR
    // =====================================================

    public void save(User user) {

        try {

            if (user.getId() != null && user.getId() > 0) {

                if (user.getPassword() == null
                        || user.getPassword().isBlank()) {

                    jdbcTemplate.update(
                            """
                            UPDATE usuarios
                            SET usuario = ?
                            WHERE id_usuario = ?
                            """,
                            user.getUsername(),
                            user.getId()
                    );

                } else {

                    String encryptedPassword =
                            PasswordHasher.sha256(user.getPassword());

                    jdbcTemplate.update(
                            """
                            UPDATE usuarios
                            SET usuario = ?, password = ?
                            WHERE id_usuario = ?
                            """,
                            user.getUsername(),
                            encryptedPassword,
                            user.getId()
                    );
                }

                registrarBitacora(
                        user.getId(),
                        user.getId(),
                        "UPDATE",
                        "Usuario modificado"
                );

            } else {

                if (user.getPassword() == null
                        || user.getPassword().isBlank()) {
                    throw new DatabaseConnectionException(
                            "La contraseña es obligatoria para un usuario nuevo.",
                            null
                    );
                }

                String encryptedPassword =
                        PasswordHasher.sha256(user.getPassword());

                jdbcTemplate.update(
                        """
                        INSERT INTO usuarios
                        (usuario, password, ingreso)
                        VALUES (?, ?, CURRENT_TIMESTAMP)
                        """,
                        user.getUsername(),
                        encryptedPassword
                );

                Long nuevoId = jdbcTemplate.queryForObject(
                        """
                        SELECT MAX(id_usuario)
                        FROM usuarios
                        """,
                        Long.class
                );

                registrarBitacora(
                        nuevoId,
                        nuevoId,
                        "CREATE",
                        "Nuevo usuario agregado"
                );
            }

        } catch (DataAccessException ex) {
            throw new DatabaseConnectionException(
                    "Error al guardar usuario.",
                    ex
            );
        }
    }

    // =====================================================
    // ELIMINAR
    // =====================================================

    public void deleteById(Long id) {

        try {

            registrarBitacora(
                    id,
                    id,
                    "BAJA",
                    "Usuario dado de baja"
            );

            jdbcTemplate.update(
                    """
                    DELETE FROM usuarios
                    WHERE id_usuario = ?
                    """,
                    id
            );

        } catch (DataAccessException ex) {
            throw new DatabaseConnectionException(
                    "Error al dar de baja usuario.",
                    ex
            );
        }
    }

    // =====================================================
    // BITÁCORA
    // =====================================================

    public void registrarBitacora(
            Long idUsuarioActivo,
            Long idUsuarioAfectado,
            String acceso,
            String descripcion) {

        try {

            jdbcTemplate.update(
                    """
                    INSERT INTO bitacora
                    (
                        id_usuario_activo,
                        id_usuario_afectado,
                        acceso,
                        descripcion,
                        fecha
                    )
                    VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
                    """,
                    idUsuarioActivo,
                    idUsuarioAfectado,
                    acceso,
                    descripcion
            );

        } catch (DataAccessException ex) {
            throw new DatabaseConnectionException(
                    "Error al registrar bitácora.",
                    ex
            );
        }
    }

    public void registrarSalida(User user) {

        registrarBitacora(
                user.getId(),
                user.getId(),
                "LOGOUT",
                "Cierre de sesión"
        );
    }
}
