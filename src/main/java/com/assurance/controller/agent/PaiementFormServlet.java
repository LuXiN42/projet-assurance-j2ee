package com.assurance.controller.agent;

import com.assurance.dao.ContratDao;
import com.assurance.model.*;
import com.assurance.service.BusinessException;
import com.assurance.service.PaiementService;
import com.assurance.util.FormUtil;
import com.assurance.util.Validateur;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

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

        // ---- Validation côté servlet (exigence section 4 du sujet) ----
        Validateur v = new Validateur();
        Long contratId      = v.id(req.getParameter("contratId"), "Le contrat");
        BigDecimal montant  = v.montant(req.getParameter("montant"), "Le montant", true, true);
        LocalDate date      = v.date(req.getParameter("date"), "La date", false);
        ModePaiement mode   = v.choix(ModePaiement.class, req.getParameter("mode"), "Le mode de paiement", false);
        StatutPaiement stat = v.choix(StatutPaiement.class, req.getParameter("statut"), "Le statut", true);

        if (v.estValide()) {
            try {
                service.enregistrer(contratId, montant, date, mode, stat);
                resp.sendRedirect(req.getContextPath() + "/agent/paiements?msg="
                        + FormUtil.enc("Paiement enregistré"));
                return;
            } catch (BusinessException e) {
                v.ajouter(e.getMessage());
            }
        }
        req.setAttribute("erreurs", v.getErreurs());
        afficher(req, resp);
    }

    private void afficher(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("contrats", contratDao.findAllComplet());
        req.setAttribute("modes", ModePaiement.values());
        req.setAttribute("aujourdhui", LocalDate.now());
        req.getRequestDispatcher(VUE).forward(req, resp);
    }
}