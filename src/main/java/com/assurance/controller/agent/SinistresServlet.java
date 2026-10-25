package com.assurance.controller.agent;

import java.io.IOException;
import java.util.List;

import com.assurance.dao.SinistreDao;
import com.assurance.model.Sinistre;
import com.assurance.model.StatutSinistre;
import com.assurance.util.FormUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/agent/sinistres")
public class SinistresServlet extends HttpServlet {

    private SinistreDao sinistreDao = new SinistreDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        StatutSinistre statut = FormUtil.parseEnum(StatutSinistre.class, request.getParameter("statut"));

        List<Sinistre> sinistres;
        if (statut == null) {
            sinistres = sinistreDao.findAllComplet();
        } else {
            sinistres = sinistreDao.findByStatut(statut);
        }

        request.setAttribute("sinistres", sinistres);
        request.setAttribute("statuts", StatutSinistre.values());
        request.setAttribute("statutChoisi", statut);

        request.getRequestDispatcher("/WEB-INF/views/agent/sinistres.jsp").forward(request, response);
    }
}