package com.assurance.controller.client;

import com.assurance.model.Utilisateur;
import com.assurance.service.ContratService;
import com.assurance.util.SessionKeys;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/client/contrats")
public class MesContratsServlet extends HttpServlet {

    private final ContratService service = new ContratService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Utilisateur u = (Utilisateur) req.getSession().getAttribute(SessionKeys.UTILISATEUR);
        service.mettreAJourContratsExpires();
        req.setAttribute("contrats", service.contratsDuClient(u.getId()));
        req.getRequestDispatcher("/WEB-INF/views/client/contrats.jsp").forward(req, resp);
    }
}