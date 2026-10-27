package com.assurance.controller.admin;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import com.assurance.dao.ContratDao;
import com.assurance.dao.UtilisateurDao;
import com.assurance.model.Role;
import com.assurance.model.Utilisateur;
import com.assurance.util.FormUtil;
import com.assurance.util.PasswordUtil;
import com.assurance.util.SessionKeys;
import com.assurance.util.Validateur;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/admin/utilisateurs/form")
public class UtilisateurFormServlet extends HttpServlet {

    private UtilisateurDao utilisateurDao = new UtilisateurDao();
    private ContratDao contratDao = new ContratDao();   

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
        Utilisateur admin = (Utilisateur) request.getSession().getAttribute(SessionKeys.UTILISATEUR);

        // ---------- validation ----------
        Validateur v = new Validateur();
        Long id       = FormUtil.parseLong(request.getParameter("id"));
        String nom    = v.texte(request.getParameter("nom"),    "Le nom",    2, 100, true);
        String prenom = v.texte(request.getParameter("prenom"), "Le prénom", 2, 100, true);
        String email  = v.email(request.getParameter("email"),  "L'email");
        Role role     = v.choix(Role.class, request.getParameter("role"), "Le rôle");

        // Mot de passe : obligatoire à la création, facultatif en modification (null = inchangé)
        String motDePasse = (id == null)
                ? v.motDePasse(request.getParameter("motDePasse"), request.getParameter("confirmation"))
                : v.motDePasseOptionnel(request.getParameter("motDePasse"), request.getParameter("confirmation"));

        // Email unique (hors utilisateur modifié)
        if (email != null) {
            Utilisateur memeEmail = utilisateurDao.findByEmail(email);
            if (memeEmail != null && !memeEmail.getId().equals(id))
                v.ajouter("Cet email est déjà utilisé.");
        }

        // L'admin ne peut pas retirer son propre rôle
        if (id != null && admin != null && id.equals(admin.getId()) && role != null && role != Role.ADMIN)
            v.ajouter("Vous ne pouvez pas retirer votre propre rôle d'administrateur.");

        //un client qui a des contrats doit rester client
        if (id != null && role != null && role != Role.CLIENT) {
            Utilisateur avant = utilisateurDao.findById(id);
            if (avant != null && avant.getRole() == Role.CLIENT && !contratDao.findByClient(id).isEmpty())
                v.ajouter("Ce client a des contrats : son rôle ne peut pas être changé.");
        }

        if (!v.estValide()) {
            Utilisateur saisie = new Utilisateur();
            saisie.setId(id);
            saisie.setNom(nom);
            saisie.setPrenom(prenom);
            saisie.setEmail(email);
            saisie.setRole(role);
            afficherFormulaire(request, response, saisie, v.getErreurs());
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
            if (motDePasse != null) {
                existant.setPasswordHash(PasswordUtil.hacher(motDePasse));
            }
            utilisateurDao.update(existant);

            //si l'admin a modifié son propre compte, on met la session à jour
            if (admin != null && id.equals(admin.getId())) {
                request.getSession().setAttribute(SessionKeys.UTILISATEUR, existant);
            }
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