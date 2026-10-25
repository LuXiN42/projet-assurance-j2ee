package com.assurance.controller.agent;

import com.assurance.model.Garantie;
import com.assurance.service.BusinessException;
import com.assurance.service.GarantieService;
import com.assurance.util.FormUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/agent/garanties/*")
public class GarantieServlet extends HttpServlet {

    private final GarantieService service = new GarantieService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();
        if (path.equals("/form")) {
            Long id = FormUtil.parseLong(req.getParameter("id"));
            try {
                if (id != null) req.setAttribute("garantie", service.trouver(id));
            } catch (BusinessException e) {
                resp.sendRedirect(req.getContextPath() + "/agent/garanties?err=" + enc(e.getMessage()));
                return;
            }
            req.getRequestDispatcher("/WEB-INF/views/agent/garantie-form.jsp").forward(req, resp);
            return;
        }
        req.setAttribute("garanties", service.lister());
        req.setAttribute("message", req.getParameter("msg"));
        req.setAttribute("erreur", req.getParameter("err"));
        req.getRequestDispatcher("/WEB-INF/views/agent/garanties.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();
        String base = req.getContextPath() + "/agent/garanties";

        if (path.equals("/supprimer")) {
            Long id = FormUtil.parseLong(req.getParameter("id"));
            try {
                service.supprimer(id);
                resp.sendRedirect(base + "?msg=" + enc("Garantie supprimée."));
            } catch (BusinessException e) {
                resp.sendRedirect(base + "?err=" + enc(e.getMessage()));
            }
            return;
        }

        // enregistrement (création ou modification)
        Long id = FormUtil.parseLong(req.getParameter("id"));
        String nom = req.getParameter("nom");
        String description = req.getParameter("description");
        try {
            if (id == null) service.creer(nom, description);
            else service.modifier(id, nom, description);
            resp.sendRedirect(base + "?msg=" + enc(id == null ? "Garantie créée." : "Garantie modifiée."));
        } catch (BusinessException e) {
            Garantie saisie = new Garantie();
            saisie.setId(id);
            saisie.setNom(nom);
            saisie.setDescription(description);
            req.setAttribute("garantie", saisie);
            req.setAttribute("erreur", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/agent/garantie-form.jsp").forward(req, resp);
        }
    }

    private static String enc(String s) { return URLEncoder.encode(s, StandardCharsets.UTF_8); }
}