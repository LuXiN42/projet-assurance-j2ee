package com.assurance.controller.agent;

import com.assurance.model.ModePaiement;
import com.assurance.service.BusinessException;
import com.assurance.service.PaiementService;
import com.assurance.util.FormUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/agent/paiements/action")
public class PaiementActionServlet extends HttpServlet {

    private final PaiementService service = new PaiementService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = FormUtil.parseLong(req.getParameter("id"));
        String action = req.getParameter("action");
        String msg;
        try {
            if (id == null || action == null) throw new BusinessException("Requête invalide.");
            switch (action) {
                case "valider" -> {
                    ModePaiement mode = FormUtil.parseEnum(ModePaiement.class, req.getParameter("mode"));
                    service.valider(id, mode);
                    msg = "Paiement validé";
                }
                case "echouer" -> { service.echouer(id);  msg = "Paiement marqué échoué"; }
                case "annuler" -> { service.annuler(id);  msg = "Paiement annulé"; }
                default -> throw new BusinessException("Action inconnue : " + action);
            }
        } catch (BusinessException e) {
            msg = "Erreur : " + e.getMessage();
        }
        resp.sendRedirect(req.getContextPath() + "/agent/paiements?msg="
                + URLEncoder.encode(msg, StandardCharsets.UTF_8));
    }
}