package com.assurance.controller.agent;

import com.assurance.model.StatutPaiement;
import com.assurance.service.PaiementService;
import com.assurance.util.FormUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/agent/paiements")
public class PaiementListeServlet extends HttpServlet {

    private final PaiementService service = new PaiementService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        StatutPaiement statut = FormUtil.parseEnum(StatutPaiement.class, req.getParameter("statut"));
        req.setAttribute("paiements", statut == null ? service.tous() : service.parStatut(statut));
        req.setAttribute("statuts", StatutPaiement.values());
        req.setAttribute("statutChoisi", statut);
        req.setAttribute("modes", com.assurance.model.ModePaiement.values());
        req.getRequestDispatcher("/WEB-INF/views/agent/paiements.jsp").forward(req, resp);
    }
}