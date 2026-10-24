package com.assurance.controller.agent;

import com.assurance.service.BusinessException;
import com.assurance.service.ContratService;
import com.assurance.util.FormUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/agent/contrats/action")
public class ContratActionServlet extends HttpServlet {

    private final ContratService service = new ContratService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = FormUtil.parseLong(req.getParameter("id"));
        String action = req.getParameter("action");
        String msg;
        try {
            if (id == null || action == null) throw new BusinessException("Paramètres manquants.");
            switch (action) {
                case "activer"   -> service.activer(id);
                case "suspendre" -> service.suspendre(id);
                case "resilier"  -> service.resilier(id);
                case "renouveler" -> {
                    Integer mois = FormUtil.parseInt(req.getParameter("dureeMois"));
                    if (mois == null) throw new BusinessException("Durée de renouvellement invalide.");
                    service.renouveler(id, mois);
                }
                default -> throw new BusinessException("Action inconnue : " + action);
            }
            msg = "msg=" + enc("Action « " + action + " » effectuée.");
        } catch (BusinessException e) {
            msg = "err=" + enc(e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/agent/contrats/detail?id=" + id + "&" + msg);
    }

    private static String enc(String s) { return URLEncoder.encode(s, StandardCharsets.UTF_8); }
}