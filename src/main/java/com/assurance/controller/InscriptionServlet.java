package com.assurance.controller;

import com.assurance.dao.UtilisateurDao;
import com.assurance.model.Role;
import com.assurance.model.Utilisateur;
import com.assurance.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@WebServlet("/inscription")
public class InscriptionServlet extends HttpServlet {

    private static final String VUE = "/WEB-INF/views/inscription.jsp";
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private final UtilisateurDao utilisateurDao = new UtilisateurDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(VUE).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String nom = trim(req.getParameter("nom"));
        String prenom = trim(req.getParameter("prenom"));
        String email = trim(req.getParameter("email")).toLowerCase();
        String mdp = req.getParameter("motDePasse");
        String confirmation = req.getParameter("confirmation");

        // ---- Validation côté servlet (exigée par le sujet) ----
        Map<String, String> erreurs = new LinkedHashMap<>();
        if (nom.isEmpty() || nom.length() > 100) erreurs.put("nom", "Nom obligatoire (100 caractères max).");
        if (prenom.isEmpty() || prenom.length() > 100) erreurs.put("prenom", "Prénom obligatoire (100 caractères max).");
        if (!EMAIL.matcher(email).matches() || email.length() > 255) erreurs.put("email", "Email invalide.");
        else if (utilisateurDao.existsByEmail(email)) erreurs.put("email", "Cet email est déjà utilisé.");
        if (mdp == null || mdp.length() < 8) erreurs.put("motDePasse", "8 caractères minimum.");
        else if (!mdp.equals(confirmation)) erreurs.put("confirmation", "Les mots de passe ne correspondent pas.");

        if (!erreurs.isEmpty()) {
            req.setAttribute("erreurs", erreurs);
            req.getRequestDispatcher(VUE).forward(req, resp);   // les champs sont réaffichés via ${param.xxx}
            return;
        }

        // ---- Création : toujours en CLIENT (jamais de rôle venant du formulaire) ----
        Utilisateur u = new Utilisateur();
        u.setNom(nom);
        u.setPrenom(prenom);
        u.setEmail(email);
        u.setPasswordHash(PasswordUtil.hacher(mdp));
        u.setRole(Role.CLIENT);
        u.setDateInscription(LocalDate.now());

        try {
            utilisateurDao.save(u);
        } catch (RuntimeException e) {   // ex. deux inscriptions simultanées avec le même email (contrainte UNIQUE)
            erreurs.put("email", "Cet email est déjà utilisé.");
            req.setAttribute("erreurs", erreurs);
            req.getRequestDispatcher(VUE).forward(req, resp);
            return;
        }

        // Redirection (pattern Post/Redirect/Get : évite une double inscription si F5)
        resp.sendRedirect(req.getContextPath() + "/login?inscrit=1");
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }
}