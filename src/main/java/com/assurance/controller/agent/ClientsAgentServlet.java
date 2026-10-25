package com.assurance.controller.agent;

import com.assurance.dao.UtilisateurDao;
import com.assurance.model.Utilisateur;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/agent/clients")
public class ClientsAgentServlet extends HttpServlet {

    private final UtilisateurDao utilisateurDao = new UtilisateurDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String q = req.getParameter("q");
        List<Utilisateur> clients = utilisateurDao.rechercherClients(q);
        req.setAttribute("clients", clients);
        req.setAttribute("q", q == null ? "" : q);
        req.getRequestDispatcher("/WEB-INF/views/agent/clients.jsp").forward(req, resp);
    }
}