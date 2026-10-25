package com.assurance.controller.admin;

import java.io.IOException;
import java.util.List;

import com.assurance.dao.UtilisateurDao;
import com.assurance.model.Role;
import com.assurance.model.Utilisateur;
import com.assurance.util.FormUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/admin/utilisateurs")
public class UtilisateursServlet extends HttpServlet {

    private UtilisateurDao utilisateurDao = new UtilisateurDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Role role = FormUtil.parseEnum(Role.class, request.getParameter("role"));

        List<Utilisateur> utilisateurs;
        if (role == null) {
            utilisateurs = utilisateurDao.findAll();
        } else {
            utilisateurs = utilisateurDao.findByRole(role);
        }

        request.setAttribute("utilisateurs", utilisateurs);
        request.setAttribute("roles", Role.values());
        request.setAttribute("roleChoisi", role);
        request.setAttribute("message", request.getParameter("message"));
        request.setAttribute("erreur", request.getParameter("erreur"));

        request.getRequestDispatcher("/WEB-INF/views/admin/utilisateurs.jsp").forward(request, response);
    }
}