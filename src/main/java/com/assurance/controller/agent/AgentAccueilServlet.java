package com.assurance.controller.agent;

import java.io.IOException;

import com.assurance.model.Role;
import com.assurance.model.Utilisateur;
import com.assurance.util.SessionKeys;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/agent/accueil")
public class AgentAccueilServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Un admin qui passe par les pages agent revient sur son propre accueil
        Utilisateur u = (Utilisateur) req.getSession().getAttribute(SessionKeys.UTILISATEUR);
        if (u.getRole() == Role.ADMIN) {
            resp.sendRedirect(req.getContextPath() + "/admin/accueil");
            return;
        }

        req.getRequestDispatcher("/WEB-INF/views/agent/accueil.jsp")
           .forward(req, resp);
    }
}