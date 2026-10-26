package com.assurance.controller.agent;

import com.assurance.service.BusinessException;
import com.assurance.service.GarantieService;
import com.assurance.util.FormUtil;
import com.assurance.util.Validateur;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/agent/contrats/garanties")
public class ContratGarantieServlet extends HttpServlet {

    private final GarantieService service = new GarantieService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Validateur v = new Validateur();
        Long contratId  = v.id(req.getParameter("contratId"), "Le contrat");
        Long garantieId = v.id(req.getParameter("garantieId"), "La garantie");
        String action   = v.parmi(req.getParameter("action"), "L'action", "associer", "dissocier");

        if (contratId == null) {
            resp.sendRedirect(req.getContextPath() + "/agent/contrats?err=" + FormUtil.enc(v.messages()));
            return;
        }

        String msg;
        if (!v.estValide()) {
            msg = "err=" + FormUtil.enc(v.messages());
        } else {
            try {
                switch (action) {
                    case "associer"  -> { service.associer(garantieId, contratId);  msg = "msg=" + FormUtil.enc("Garantie associée."); }
                    case "dissocier" -> { service.dissocier(garantieId, contratId); msg = "msg=" + FormUtil.enc("Garantie retirée."); }
                    default -> throw new BusinessException("Action inconnue : " + action);
                }
            } catch (BusinessException e) {
                msg = "err=" + FormUtil.enc(e.getMessage());
            }
        }
        resp.sendRedirect(req.getContextPath() + "/agent/contrats/detail?id=" + contratId + "&" + msg);
    }
}