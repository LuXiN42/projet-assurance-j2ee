package com.assurance.controller.admin;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.assurance.dao.ContratDao;
import com.assurance.dao.UtilisateurDao;
import com.assurance.model.Utilisateur;
import com.assurance.util.FormUtil;
import com.assurance.util.SessionKeys;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/admin/utilisateurs/supprimer")
public class UtilisateurSupprimerServlet extends HttpServlet {

    private UtilisateurDao utilisateurDao = new UtilisateurDao();
    private ContratDao contratDao = new ContratDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long id = FormUtil.parseLong(request.getParameter("id"));
        Utilisateur admin = (Utilisateur) request.getSession().getAttribute(SessionKeys.UTILISATEUR);
        String parametre;

        if (id == null || utilisateurDao.findById(id) == null) {
            parametre = "erreur=" + encoder("Utilisateur introuvable.");
        } else if (id.equals(admin.getId())) {
            parametre = "erreur=" + encoder("Vous ne pouvez pas supprimer votre propre compte.");
        } else if (!contratDao.findByClient(id).isEmpty()) {
            parametre = "erreur=" + encoder("Impossible de supprimer ce client : il a des contrats.");
        } else {
            try {
                utilisateurDao.deleteById(id);
                parametre = "message=" + encoder("Utilisateur supprimé.");
            } catch (RuntimeException e) {
                // par exemple un agent qui a écrit des commentaires sur des sinistres
                parametre = "erreur=" + encoder("Impossible de supprimer cet utilisateur : il est lié à d'autres données.");
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/utilisateurs?" + parametre);
    }

    private String encoder(String texte) {
        return URLEncoder.encode(texte, StandardCharsets.UTF_8);
    }
}