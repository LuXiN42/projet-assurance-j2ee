package com.assurance.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {
    private PasswordUtil() {}

    /** Transforme un mot de passe en clair en hash BCrypt. */
    public static String hacher(String motDePasse) {
        return BCrypt.hashpw(motDePasse, BCrypt.gensalt(12));
    }

    /** Vérifie un mot de passe saisi contre le hash stocké. */
    public static boolean verifier(String motDePasse, String hash) {
        if (motDePasse == null || hash == null) return false;
        try {
            return BCrypt.checkpw(motDePasse, hash);
        } catch (IllegalArgumentException e) {   // hash mal formé en base
            return false;
        }
    }
}