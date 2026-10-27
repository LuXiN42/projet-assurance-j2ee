package com.assurance.controller.client;

import java.io.IOException;

import com.assurance.dao.UtilisateurDao;
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

@WebServlet("/profil")
public class ProfilServlet extends HttpServlet {

    private UtilisateurDao utilisateurDao = new UtilisateurDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Utilisateur connecte = (Utilisateur) request.getSession().getAttribute(SessionKeys.UTILISATEUR);

        Utilisateur compte = utilisateurDao.findById(connecte.getId());

        request.setAttribute("compte", compte);
        request.setAttribute("message", request.getParameter("message"));
        request.getRequestDispatcher("/WEB-INF/views/client/profil.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        Utilisateur connecte = (Utilisateur) request.getSession().getAttribute(SessionKeys.UTILISATEUR);
        Utilisateur compte = utilisateurDao.findById(connecte.getId());

        Validateur v = new Validateur();
        String nom       = v.texte(request.getParameter("nom"), "Le nom", 2, 100, true);
        String prenom    = v.texte(request.getParameter("prenom"), "Le prénom", 2, 100, true);
        String email     = v.email(request.getParameter("email"), "L'email");
        String telephone = v.telephone(request.getParameter("telephone"), "Le téléphone", false);
        String adresse   = v.texte(request.getParameter("adresse"), "L'adresse", 5, 255, false);

        // L'email doit rester unique
        if (email != null) {
            Utilisateur memeEmail = utilisateurDao.findByEmail(email);
            if (memeEmail != null && !memeEmail.getId().equals(compte.getId()))
                v.ajouter("Cet email est déjà utilisé.");
        }

        // Changement de mot de passe : seulement si un nouveau est saisi
        String ancien = request.getParameter("ancienMotDePasse");
        String nouveau = v.motDePasseOptionnel(request.getParameter("nouveauMotDePasse"),
                                               request.getParameter("confirmation"));
        if (nouveau != null && !PasswordUtil.verifier(ancien, compte.getPasswordHash())) {
            v.ajouter("Le mot de passe actuel est incorrect.");
        }

        if (!v.estValide()) {
            compte.setNom(nom);
            compte.setPrenom(prenom);
            compte.setEmail(email);
            compte.setTelephone(telephone);
            compte.setAdresse(adresse);
            request.setAttribute("compte", compte);
            request.setAttribute("erreurs", v.getErreurs());
            request.getRequestDispatcher("/WEB-INF/views/client/profil.jsp").forward(request, response);
            return;
        }

        compte.setNom(nom);
        compte.setPrenom(prenom);
        compte.setEmail(email);
        compte.setTelephone(telephone);
        compte.setAdresse(adresse);
        if (nouveau != null) {
            compte.setPasswordHash(PasswordUtil.hacher(nouveau));
        }
        utilisateurDao.update(compte);

        // On met aussi à jour la copie rangée dans la session
        request.getSession().setAttribute(SessionKeys.UTILISATEUR, compte);

        response.sendRedirect(request.getContextPath() + "/profil?message="
                + FormUtil.enc("Profil mis à jour."));
    }
}