package com.assurance.controller.agent;

import com.assurance.dao.ContratDao;
import com.assurance.model.*;
import com.assurance.service.BusinessException;
import com.assurance.service.PaiementService;
import com.assurance.util.FormUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/agent/paiements/form")
public class PaiementFormServlet extends HttpServlet {

    private static final String VUE = "/WEB-INF/views/agent/paiement-form.jsp";

    private final PaiementService service = new PaiementService();
    private final ContratDao contratDao = new ContratDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        afficher(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        Long contratId       = FormUtil.parseLong(req.getParameter("contratId"));
        BigDecimal montant   = FormUtil.parseBigDecimal(req.getParameter("montant"));
        LocalDate date       = FormUtil.parseDate(req.getParameter("date"));
        ModePaiement mode    = FormUtil.parseEnum(ModePaiement.class, req.getParameter("mode"));
        StatutPaiement stat  = FormUtil.parseEnum(StatutPaiement.class, req.getParameter("statut"));

        List<String> erreurs = new ArrayList<>();
        if (contratId == null) erreurs.add("Contrat manquant.");
        if (montant == null) erreurs.add("Montant manquant ou invalide.");
        else if (montant.signum() <= 0) erreurs.add("Le montant doit être positif.");
        if (!FormUtil.isBlank(req.getParameter("date")) && date == null)
            erreurs.add("Date invalide (format AAAA-MM-JJ).");
        if (!FormUtil.isBlank(req.getParameter("mode")) && mode == null)
            erreurs.add("Mode de paiement invalide.");
        if (stat == null) erreurs.add("Statut manquant ou invalide.");

        if (erreurs.isEmpty()) {
            try {
                service.enregistrer(contratId, montant, date, mode, stat);
                resp.sendRedirect(req.getContextPath() + "/agent/paiements?msg=Paiement+enregistr%C3%A9");
                return;
            } catch (BusinessException e) {
                erreurs.add(e.getMessage());
            }
        }
        req.setAttribute("erreurs", erreurs);
        afficher(req, resp);
    }

    private void afficher(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("contrats", contratDao.findAllComplet());
        req.setAttribute("modes", ModePaiement.values());
        // préremplissage depuis ?contratId=... (lien depuis la fiche contrat)
        req.setAttribute("aujourdhui", LocalDate.now());
        req.getRequestDispatcher(VUE).forward(req, resp);
    }
}