package com.assurance.controller.agent;

import com.assurance.dao.TypeAssuranceDao;
import com.assurance.model.StatutContrat;
import com.assurance.service.ContratService;
import com.assurance.util.FormUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/agent/contrats")
public class ContratsServlet extends HttpServlet {

    private final ContratService service = new ContratService();
    private final TypeAssuranceDao typeDao = new TypeAssuranceDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        service.mettreAJourContratsExpires();

        String numero    = req.getParameter("numero");
        String nomClient = req.getParameter("nomClient");
        Long typeId      = FormUtil.parseLong(req.getParameter("typeId"));
        StatutContrat st = FormUtil.parseEnum(StatutContrat.class, req.getParameter("statut"));

        req.setAttribute("contrats", service.rechercher(numero, nomClient, typeId, st));
        req.setAttribute("types", typeDao.findAll());
        req.setAttribute("statuts", StatutContrat.values());
        req.setAttribute("message", req.getParameter("msg"));
        req.getRequestDispatcher("/WEB-INF/views/agent/contrats.jsp").forward(req, resp);
    }
}