package com.assurance.controller;

import com.assurance.dao.UtilisateurDao;
import com.assurance.model.Role;
import com.assurance.model.Utilisateur;
import com.assurance.util.PasswordUtil;
import com.assurance.util.Validateur;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/inscription")
public class InscriptionServlet extends HttpServlet {

    private static final String VUE = "/WEB-INF/views/inscription.jsp";
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

        // ---- Validation commune (exigée par le sujet) ----
        Validateur v = new Validateur();
        String nom    = v.texte(req.getParameter("nom"),    "Le nom",    2, 100, true);
        String prenom = v.texte(req.getParameter("prenom"), "Le prénom", 2, 100, true);
        String email  = v.email(req.getParameter("email"),  "L'email");
        String mdp    = v.motDePasse(req.getParameter("motDePasse"), req.getParameter("confirmation"));

        // ---- Règle métier : email unique ----
        if (email != null && utilisateurDao.existsByEmail(email))
            v.ajouter("Cet email est déjà utilisé.");

        if (!v.estValide()) {
            req.setAttribute("erreurs", v.getErreurs());
            req.getRequestDispatcher(VUE).forward(req, resp);   // champs réaffichés via ${param.xxx}
            return;
        }

        // ---- Création : toujours CLIENT (jamais de rôle venant du formulaire) ----
        Utilisateur u = new Utilisateur();
        u.setNom(nom);
        u.setPrenom(prenom);
        u.setEmail(email);
        u.setPasswordHash(PasswordUtil.hacher(mdp));
        u.setRole(Role.CLIENT);
        u.setDateInscription(LocalDate.now());

        try {
            utilisateurDao.save(u);
        } catch (RuntimeException e) {   // contrainte UNIQUE (inscriptions simultanées)
            v.ajouter("Cet email est déjà utilisé.");
            req.setAttribute("erreurs", v.getErreurs());
            req.getRequestDispatcher(VUE).forward(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/login?inscrit=1");   // PRG
    }
}