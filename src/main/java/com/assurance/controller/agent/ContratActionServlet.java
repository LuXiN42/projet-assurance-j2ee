package com.assurance.controller.agent;

import com.assurance.service.BusinessException;
import com.assurance.service.ContratService;
import com.assurance.util.FormUtil;
import com.assurance.util.Validateur;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/agent/contrats/action")
public class ContratActionServlet extends HttpServlet {

    private final ContratService service = new ContratService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Validateur v = new Validateur();
        Long id       = v.id(req.getParameter("id"), "Le contrat");
        String action = v.parmi(req.getParameter("action"), "L'action",
                                "activer", "suspendre", "resilier", "renouveler");
        Integer mois  = "renouveler".equals(action)
                ? v.entier(req.getParameter("dureeMois"), "La durée de renouvellement", 1, 120, true)
                : null;

        // Sans id valide, on ne peut pas revenir sur le détail : retour à la liste
        if (id == null) {
            resp.sendRedirect(req.getContextPath() + "/agent/contrats?err=" + FormUtil.enc(v.messages()));
            return;
        }

        String msg;
        if (!v.estValide()) {
            msg = "err=" + FormUtil.enc(v.messages());
        } else {
            try {
                switch (action) {
                    case "activer"    -> service.activer(id);
                    case "suspendre"  -> service.suspendre(id);
                    case "resilier"   -> service.resilier(id);
                    case "renouveler" -> service.renouveler(id, mois);
                    default -> throw new BusinessException("Action inconnue : " + action);
                }
                msg = "msg=" + FormUtil.enc("Action « " + action + " » effectuée.");
            } catch (BusinessException e) {
                msg = "err=" + FormUtil.enc(e.getMessage());
            }
        }
        resp.sendRedirect(req.getContextPath() + "/agent/contrats/detail?id=" + id + "&" + msg);
    }
}