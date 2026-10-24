package com.assurance.controller;

import com.assurance.dao.ContratDao;
import com.assurance.dao.SinistreDao;
import com.assurance.model.*;
import com.assurance.service.BusinessException;
import com.assurance.service.SinistreService;
import com.assurance.util.SessionKeys;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@WebServlet("/client/sinistres/declarer")
@MultipartConfig(
        maxFileSize = 5 * 1024 * 1024,        // 5 Mo par fichier
        maxRequestSize = 25 * 1024 * 1024     // 25 Mo pour toute la requête
)
public class DeclarerSinistreServlet extends HttpServlet {

    private static final String VUE = "/WEB-INF/views/client/declarer-sinistre.jsp";
    private static final Path DOSSIER_UPLOADS = Paths.get(System.getProperty("user.home"), "assurance-uploads");
    private static final Set<String> EXTENSIONS = Set.of("pdf", "jpg", "jpeg", "png");
    private static final int MAX_FICHIERS = 5;

    private final ContratDao contratDao = new ContratDao();
    private final SinistreService sinistreService = new SinistreService(new SinistreDao(), contratDao);
    // ===================== GET : afficher le formulaire =====================

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Utilisateur u = clientConnecte(req, resp);
        if (u == null) return;

        req.setAttribute("contrats", contratDao.findActifsByClient(u.getId()));
        req.setAttribute("aujourdhui", LocalDate.now());
        req.getRequestDispatcher(VUE).forward(req, resp);
    }

    // ===================== POST : enregistrer la déclaration =====================

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Utilisateur u = clientConnecte(req, resp);
            if (u == null) return;
            try {
                req.getParts();
            } catch (IllegalStateException e) {
                erreur(req, resp, u, "Fichier trop volumineux (5 Mo maximum par fichier, 25 Mo au total).");
                return;
            }

        // ---------- 1. Lecture et validation des champs ----------
        String description  = trim(req.getParameter("description"));
        String typeSinistre = trim(req.getParameter("typeSinistre"));

        Long contratId;
        try {
            contratId = Long.parseLong(req.getParameter("contratId"));
        } catch (NumberFormatException e) {
            erreur(req, resp, u, "Veuillez choisir un contrat.");
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(req.getParameter("date"));
        } catch (DateTimeParseException | NullPointerException e) {
            erreur(req, resp, u, "Date invalide.");
            return;
        }

        BigDecimal montantEstime = null;
        String montantTxt = trim(req.getParameter("montantEstime"));
        if (!montantTxt.isEmpty()) {
            try {
                montantEstime = new BigDecimal(montantTxt.replace(',', '.'));
            } catch (NumberFormatException e) {
                erreur(req, resp, u, "Le montant estimé doit être un nombre positif.");
                return;
            }
        }

        // ---------- 2. Validation des fichiers (avant toute écriture) ----------
        List<Part> fichiers = new ArrayList<>();
        for (Part p : req.getParts()) {
            if ("pieces".equals(p.getName()) && p.getSize() > 0) {
                fichiers.add(p);
            }
        }
        if (fichiers.size() > MAX_FICHIERS) {
            erreur(req, resp, u, "Maximum " + MAX_FICHIERS + " fichiers.");
            return;
        }
        for (Part p : fichiers) {
            if (!EXTENSIONS.contains(extension(p.getSubmittedFileName()))) {
                erreur(req, resp, u, "Format refusé : " + p.getSubmittedFileName() + " (PDF, JPG ou PNG uniquement).");
                return;
            }
        }

        // ---------- 3. Écriture des fichiers sur disque (avant validation métier) ----------
        Files.createDirectories(DOSSIER_UPLOADS);
        List<Path> ecrits = new ArrayList<>();
        List<PieceJustificative> pieces = new ArrayList<>();
        for (Part p : fichiers) {
            String nomStocke = UUID.randomUUID() + "." + extension(p.getSubmittedFileName());
            Path cible = DOSSIER_UPLOADS.resolve(nomStocke);
            try (InputStream in = p.getInputStream()) {
                Files.copy(in, cible);
            }
            ecrits.add(cible);

            PieceJustificative piece = new PieceJustificative();
            piece.setNomOriginal(Paths.get(p.getSubmittedFileName()).getFileName().toString());
            piece.setNomStocke(nomStocke);
            piece.setTypeMime(p.getContentType());
            piece.setTaille(p.getSize());
            pieces.add(piece);
        }

        // ---------- 4. Délégation au service métier ----------
        Sinistre s;
        try {
            s = sinistreService.declarer(
                    u.getId(),
                    contratId,
                    date,
                    description,
                    typeSinistre,
                    montantEstime,
                    pieces);
        } catch (BusinessException e) {
            // Si la validation métier échoue, on supprime les fichiers déjà écrits.
            for (Path f : ecrits) {
                try { Files.deleteIfExists(f); } catch (IOException ignored) {}
            }
            erreur(req, resp, u, e.getMessage());
            return;
        } catch (RuntimeException e) {
            for (Path f : ecrits) {
                try { Files.deleteIfExists(f); } catch (IOException ignored) {}
            }
            log("Erreur lors de la déclaration du sinistre", e);
            erreur(req, resp, u, "Une erreur est survenue, la déclaration n'a pas été enregistrée.");
            return;
        }

        // Pattern PRG (Post-Redirect-Get) : évite une double déclaration si l'utilisateur actualise la page
        req.getSession().setAttribute("message", "Sinistre " + s.getNumeroSinistre() + " déclaré avec succès.");
        resp.sendRedirect(req.getContextPath() + "/client/accueil");
    }

    // ===================== Méthodes utilitaires =====================

    /** Renvoie le client connecté, ou redirige vers /login et renvoie null. */
    private Utilisateur clientConnecte(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        Utilisateur u = session == null ? null : (Utilisateur) session.getAttribute(SessionKeys.UTILISATEUR);
        if (u == null || u.getRole() != Role.CLIENT) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        return u;
    }

    /** Réaffiche le formulaire avec un message d'erreur et les contrats du client. */
    private void erreur(HttpServletRequest req, HttpServletResponse resp, Utilisateur u, String message)
            throws ServletException, IOException {
        req.setAttribute("erreur", message);
        req.setAttribute("contrats", contratDao.findActifsByClient(u.getId()));
        req.setAttribute("aujourdhui", LocalDate.now());
        req.getRequestDispatcher(VUE).forward(req, resp);
    }

    private static String trim(String v) {
        return v == null ? "" : v.trim();
    }

    private static String extension(String nomFichier) {
        if (nomFichier == null) return "";
        int i = nomFichier.lastIndexOf('.');
        return i < 0 ? "" : nomFichier.substring(i + 1).toLowerCase();
    }

    private static void supprimerFichiers(List<Path> fichiers) {
    for (Path f : fichiers) {
        try { Files.deleteIfExists(f); } catch (IOException ignored) {}
    }
    }
}