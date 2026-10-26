package com.assurance.controller.agent;

import com.assurance.model.ModePaiement;
import com.assurance.service.BusinessException;
import com.assurance.service.PaiementService;
import com.assurance.util.FormUtil;
import com.assurance.util.Validateur;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/agent/paiements/action")
public class PaiementActionServlet extends HttpServlet {

    private final PaiementService service = new PaiementService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Validateur v = new Validateur();
        Long id       = v.id(req.getParameter("id"), "Le paiement");
        String action = v.parmi(req.getParameter("action"), "L'action", "valider", "echouer", "annuler");
        ModePaiement mode = "valider".equals(action)
                ? v.choix(ModePaiement.class, req.getParameter("mode"), "Le mode de paiement", false)
                : null;

        String msg;
        if (!v.estValide()) {
            msg = "Erreur : " + v.messages();
        } else {
            try {
                switch (action) {
                    case "valider" -> { service.valider(id, mode); msg = "Paiement validé"; }
                    case "echouer" -> { service.echouer(id);       msg = "Paiement marqué échoué"; }
                    case "annuler" -> { service.annuler(id);       msg = "Paiement annulé"; }
                    default -> throw new BusinessException("Action inconnue : " + action);
                }
            } catch (BusinessException e) {
                msg = "Erreur : " + e.getMessage();
            }
        }
        resp.sendRedirect(req.getContextPath() + "/agent/paiements?msg=" + FormUtil.enc(msg));
    }
}