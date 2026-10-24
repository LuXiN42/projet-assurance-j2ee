package com.assurance.util;

import java.nio.file.*;
import java.util.Set;

public final class UploadConfig {
    public static final Path DOSSIER =
            Paths.get(System.getProperty("user.home"), "assurance-uploads");
    public static final long TAILLE_MAX = 5L * 1024 * 1024; // 5 Mo par fichier
    public static final Set<String> TYPES_AUTORISES =
            Set.of("application/pdf", "image/jpeg", "image/png");

    static {
        try { Files.createDirectories(DOSSIER); }
        catch (Exception e) { throw new RuntimeException("Impossible de créer " + DOSSIER, e); }
    }
    private UploadConfig() {}
}