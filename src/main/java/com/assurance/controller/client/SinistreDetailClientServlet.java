package com.assurance.controller.client;

import java.io.IOException;

import com.assurance.dao.SinistreDao;
import com.assurance.model.Sinistre;
import com.assurance.model.Utilisateur;
import com.assurance.util.FormUtil;
import com.assurance.util.SessionKeys;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/client/sinistres/detail")
public class SinistreDetailClientServlet extends HttpServlet {

    private SinistreDao sinistreDao = new SinistreDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Utilisateur client = (Utilisateur) request.getSession().getAttribute(SessionKeys.UTILISATEUR);
        Long id = FormUtil.parseLong(request.getParameter("id"));

        Sinistre sinistre = null;
        if (id != null) {
            sinistre = sinistreDao.findByIdAvecPieces(id);
        }

        // Le client ne doit voir que ses propres sinistres
        if (sinistre == null || !sinistre.getContrat().getClient().getId().equals(client.getId())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Sinistre introuvable");
            return;
        }

        request.setAttribute("sinistre", sinistre);
        request.getRequestDispatcher("/WEB-INF/views/client/sinistre-detail.jsp").forward(request, response);
    }
}