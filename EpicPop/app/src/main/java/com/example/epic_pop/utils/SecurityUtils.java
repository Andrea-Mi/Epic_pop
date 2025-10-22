package com.example.epic_pop.utils;

import android.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SecurityUtils {

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 32;

    /**
     * Genera un salt aleatorio para el hash de contraseña
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.encodeToString(salt, Base64.DEFAULT);
    }

    /**
     * Cifra una contraseña con salt usando SHA-256
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(Base64.decode(salt, Base64.DEFAULT));
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.encodeToString(hashedPassword, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al cifrar contraseña", e);
        }
    }

    /**
     * Verifica si una contraseña coincide con el hash almacenado
     */
    public static boolean verifyPassword(String password, String storedHash, String salt) {
        String hashedPassword = hashPassword(password, salt);
        return hashedPassword.equals(storedHash);
    }

    /**
     * Valida el formato de email
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailPattern = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        return email.matches(emailPattern);
    }

    /**
     * Valida la seguridad de la contraseña
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperCase = true;
            else if (Character.isLowerCase(c)) hasLowerCase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if ("!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(c) >= 0) hasSpecialChar = true;
        }

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }

    /**
     * Genera un token seguro para recuperación de contraseña
     */
    public static String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] token = new byte[32];
        random.nextBytes(token);
        return Base64.encodeToString(token, Base64.DEFAULT);
    }
}
