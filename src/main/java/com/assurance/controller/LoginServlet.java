package com.assurance.controller;

import com.assurance.dao.UtilisateurDao;
import com.assurance.model.Utilisateur;
import com.assurance.util.PasswordUtil;
import com.assurance.util.SessionKeys;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String VUE = "/WEB-INF/views/login.jsp";
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
        String email = req.getParameter("email") == null ? "" : req.getParameter("email").trim().toLowerCase();
        String mdp = req.getParameter("motDePasse");

        Utilisateur u = email.isEmpty() ? null : utilisateurDao.findByEmail(email);

        // Même message dans les deux cas : on ne révèle pas si l'email existe
        if (u == null || !PasswordUtil.verifier(mdp, u.getPasswordHash())) {
            req.setAttribute("erreur", "Email ou mot de passe incorrect.");
            req.getRequestDispatcher(VUE).forward(req, resp);
            return;
        }

        HttpSession session = req.getSession();
        req.changeSessionId();                       // protection contre la fixation de session
        session.setAttribute(SessionKeys.UTILISATEUR, u);

        String cible = switch (u.getRole()) {
            case ADMIN -> "/admin/accueil";
            case AGENT -> "/agent/accueil";
            default    -> "/client/accueil";
        };
        resp.sendRedirect(req.getContextPath() + cible);
    }
}