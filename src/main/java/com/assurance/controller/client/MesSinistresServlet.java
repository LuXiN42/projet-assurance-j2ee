package com.assurance.controller.client;

import java.io.IOException;
import java.util.List;

import com.assurance.dao.SinistreDao;
import com.assurance.model.Sinistre;
import com.assurance.model.Utilisateur;
import com.assurance.util.SessionKeys;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/client/sinistres")
public class MesSinistresServlet extends HttpServlet {

    private SinistreDao sinistreDao = new SinistreDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Utilisateur client = (Utilisateur) request.getSession().getAttribute(SessionKeys.UTILISATEUR);

        List<Sinistre> sinistres = sinistreDao.findByClient(client.getId());

        request.setAttribute("sinistres", sinistres);
        request.getRequestDispatcher("/WEB-INF/views/client/mes-sinistres.jsp").forward(request, response);
    }
}