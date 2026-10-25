package com.assurance.controller.agent;

import com.assurance.service.BusinessException;
import com.assurance.service.GarantieService;
import com.assurance.util.FormUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/agent/contrats/garanties")
public class ContratGarantieServlet extends HttpServlet {

    private final GarantieService service = new GarantieService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long contratId = FormUtil.parseLong(req.getParameter("contratId"));
        Long garantieId = FormUtil.parseLong(req.getParameter("garantieId"));
        String action = req.getParameter("action");
        String msg;
        try {
            if (contratId == null || garantieId == null || action == null) {
                throw new BusinessException("Paramètres manquants.");
            }
            switch (action) {
                case "associer"  -> { service.associer(garantieId, contratId);  msg = "msg=" + enc("Garantie associée."); }
                case "dissocier" -> { service.dissocier(garantieId, contratId); msg = "msg=" + enc("Garantie retirée."); }
                default -> throw new BusinessException("Action inconnue : " + action);
            }
        } catch (BusinessException e) {
            msg = "err=" + enc(e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/agent/contrats/detail?id=" + contratId + "&" + msg);
    }

    private static String enc(String s) { return URLEncoder.encode(s, StandardCharsets.UTF_8); }
}