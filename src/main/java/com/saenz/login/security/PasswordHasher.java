package com.saenz.login.security;

// =======================================
// IMPORTACIONES
// =======================================

// CODIFICACION UTF-8
import java.nio.charset.StandardCharsets;

// CLASE PARA GENERAR HASH
import java.security.MessageDigest;

// EXCEPCION SI SHA-256 NO EXISTE
import java.security.NoSuchAlgorithmException;

// =======================================
// CLASE FINAL
// =======================================

// final = NO SE PUEDE HEREDAR

public final class PasswordHasher {

    // =======================================
    // CONSTRUCTOR PRIVADO
    // =======================================

    // EVITA CREAR OBJETOS DE ESTA CLASE

    private PasswordHasher() {
    }

    // =======================================
    // METODO SHA-256
    // =======================================

    // CONVIERTE TEXTO A HASH SHA-256

    public static String sha256(String value) {

        try {

            // =======================================
            // CREAR ALGORITMO SHA-256
            // =======================================

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            // =======================================
            // CONVERTIR TEXTO A BYTES UTF-8
            // =======================================

            byte[] hash =
                    digest.digest(
                            value.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            // =======================================
            // CREAR STRING HEXADECIMAL
            // =======================================

            StringBuilder result =
                    new StringBuilder(hash.length * 2);

            // =======================================
            // RECORRER BYTES
            // =======================================

            for (byte item : hash) {

                // CONVERTIR BYTE A HEXADECIMAL

                result.append(
                        String.format("%02x", item)
                );
            }

            // =======================================
            // RETORNAR HASH FINAL
            // =======================================

            return result.toString();

        } catch (NoSuchAlgorithmException ex) {

            // =======================================
            // ERROR SI SHA-256 NO EXISTE
            // =======================================

            throw new IllegalStateException(

                    "SHA-256 no esta disponible en esta JVM.",

                    ex
            );
        }
    }
}