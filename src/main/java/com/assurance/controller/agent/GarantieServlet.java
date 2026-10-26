package com.assurance.controller.agent;

import com.assurance.model.Garantie;
import com.assurance.service.BusinessException;
import com.assurance.service.GarantieService;
import com.assurance.util.FormUtil;
import com.assurance.util.Validateur;

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
                Validateur v = new Validateur();
                Long id = v.id(req.getParameter("id"), "La garantie");
                if (!v.estValide()) {
                    resp.sendRedirect(base + "?err=" + FormUtil.enc(v.messages()));
                    return;
                }
                try {
                    service.supprimer(id);
                    resp.sendRedirect(base + "?msg=" + FormUtil.enc("Garantie supprimée."));
                } catch (BusinessException e) {
                    resp.sendRedirect(base + "?err=" + FormUtil.enc(e.getMessage()));
                }
                return;
            }

            // enregistrement (création ou modification) ; id caché : null = création
            Long id = FormUtil.parseLong(req.getParameter("id"));
            Validateur v = new Validateur();
            String nom         = v.texte(req.getParameter("nom"), "Le nom", 2, 100, true);
            String description = v.texte(req.getParameter("description"), "La description", 0, 500, false);

            if (!v.estValide()) {
                reafficher(req, resp, id, req.getParameter("nom"), req.getParameter("description"), v.messages());
                return;
            }
            try {
                if (id == null) service.creer(nom, description);
                else service.modifier(id, nom, description);
                resp.sendRedirect(base + "?msg=" + FormUtil.enc(id == null ? "Garantie créée." : "Garantie modifiée."));
            } catch (BusinessException e) {
                reafficher(req, resp, id, nom, description, e.getMessage());
            }
        }

        /** Réaffiche le formulaire avec la saisie de l'utilisateur et le message d'erreur. */
        private void reafficher(HttpServletRequest req, HttpServletResponse resp, Long id,
                                String nom, String description, String erreur)
                throws ServletException, IOException {
            Garantie saisie = new Garantie();
            saisie.setId(id);
            saisie.setNom(nom);
            saisie.setDescription(description);
            req.setAttribute("garantie", saisie);
            req.setAttribute("erreur", erreur);
            req.getRequestDispatcher("/WEB-INF/views/agent/garantie-form.jsp").forward(req, resp);
        }

    private static String enc(String s) { return URLEncoder.encode(s, StandardCharsets.UTF_8); }
}