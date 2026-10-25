package com.assurance.controller.admin;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.assurance.dao.UtilisateurDao;
import com.assurance.model.Role;
import com.assurance.model.Utilisateur;
import com.assurance.util.FormUtil;
import com.assurance.util.PasswordUtil;
import com.assurance.util.SessionKeys;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/admin/utilisateurs/form")
public class UtilisateurFormServlet extends HttpServlet {

    private UtilisateurDao utilisateurDao = new UtilisateurDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = FormUtil.parseLong(request.getParameter("id"));
        Utilisateur utilisateur = null;

        if (id != null) {
            utilisateur = utilisateurDao.findById(id);
            if (utilisateur == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Utilisateur introuvable");
                return;
            }
        }

        afficherFormulaire(request, response, utilisateur, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Long id = FormUtil.parseLong(request.getParameter("id"));
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String email = request.getParameter("email");
        String motDePasse = request.getParameter("motDePasse");
        Role role = FormUtil.parseEnum(Role.class, request.getParameter("role"));

        if (nom != null) nom = nom.trim();
        if (prenom != null) prenom = prenom.trim();
        if (email != null) email = email.trim().toLowerCase();

        Utilisateur admin = (Utilisateur) request.getSession().getAttribute(SessionKeys.UTILISATEUR);

        // Vérification des champs
        List<String> erreurs = new ArrayList<>();

        if (nom == null || nom.isEmpty() || nom.length() > 100) {
            erreurs.add("Le nom est obligatoire (100 caractères max).");
        }
        if (prenom == null || prenom.isEmpty() || prenom.length() > 100) {
            erreurs.add("Le prénom est obligatoire (100 caractères max).");
        }
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            erreurs.add("L'email n'est pas valide.");
        } else {
            Utilisateur memeEmail = utilisateurDao.findByEmail(email);
            if (memeEmail != null && !memeEmail.getId().equals(id)) {
                erreurs.add("Cet email est déjà utilisé.");
            }
        }
        if (role == null) {
            erreurs.add("Le rôle est obligatoire.");
        }

        // Le mot de passe est obligatoire à la création, facultatif en modification
        boolean motDePasseRempli = motDePasse != null && !motDePasse.isEmpty();
        if (id == null && !motDePasseRempli) {
            erreurs.add("Le mot de passe est obligatoire pour un nouveau compte.");
        }
        if (motDePasseRempli && motDePasse.length() < 8) {
            erreurs.add("Le mot de passe doit faire au moins 8 caractères.");
        }

        // L'admin ne peut pas s'enlever lui-même le rôle admin
        if (id != null && id.equals(admin.getId()) && role != Role.ADMIN) {
            erreurs.add("Vous ne pouvez pas retirer votre propre rôle d'administrateur.");
        }

        if (!erreurs.isEmpty()) {
            Utilisateur saisie = new Utilisateur();
            saisie.setId(id);
            saisie.setNom(nom);
            saisie.setPrenom(prenom);
            saisie.setEmail(email);
            saisie.setRole(role);
            afficherFormulaire(request, response, saisie, erreurs);
            return;
        }

        String message;
        if (id == null) {
            Utilisateur nouveau = new Utilisateur();
            nouveau.setNom(nom);
            nouveau.setPrenom(prenom);
            nouveau.setEmail(email);
            nouveau.setRole(role);
            nouveau.setPasswordHash(PasswordUtil.hacher(motDePasse));
            nouveau.setDateInscription(LocalDate.now());
            utilisateurDao.save(nouveau);
            message = "Utilisateur créé.";
        } else {
            Utilisateur existant = utilisateurDao.findById(id);
            if (existant == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Utilisateur introuvable");
                return;
            }
            existant.setNom(nom);
            existant.setPrenom(prenom);
            existant.setEmail(email);
            existant.setRole(role);
            if (motDePasseRempli) {
                existant.setPasswordHash(PasswordUtil.hacher(motDePasse));
            }
            utilisateurDao.update(existant);
            message = "Utilisateur modifié.";
        }

        response.sendRedirect(request.getContextPath() + "/admin/utilisateurs?message="
                + URLEncoder.encode(message, StandardCharsets.UTF_8));
    }

    private void afficherFormulaire(HttpServletRequest request, HttpServletResponse response,
                                    Utilisateur utilisateur, List<String> erreurs)
            throws ServletException, IOException {
        request.setAttribute("compte", utilisateur);
        request.setAttribute("roles", Role.values());
        request.setAttribute("erreurs", erreurs);
        request.getRequestDispatcher("/WEB-INF/views/admin/utilisateur-form.jsp").forward(request, response);
    }
}