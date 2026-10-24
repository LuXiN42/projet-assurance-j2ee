package com.assurance.controller.client;

import com.assurance.model.Role;
import com.assurance.model.Utilisateur;
import com.assurance.service.PaiementService;
import com.assurance.util.SessionKeys;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/client/paiements")
public class MesPaiementsServlet extends HttpServlet {

    private static final int JOURS_ECHEANCES = 30;
    private final PaiementService service = new PaiementService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Utilisateur u = (Utilisateur) req.getSession().getAttribute(SessionKeys.UTILISATEUR);
        if (u == null || u.getRole() != Role.CLIENT) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("effectues",  service.effectuesClient(u.getId()));
        req.setAttribute("enAttente",  service.enAttenteClient(u.getId()));
        req.setAttribute("echeances",  service.echeancesClient(u.getId(), JOURS_ECHEANCES));
        req.setAttribute("historique", service.historiqueClient(u.getId()));
        req.setAttribute("jours", JOURS_ECHEANCES);
        req.getRequestDispatcher("/WEB-INF/views/client/mes-paiements.jsp").forward(req, resp);
    }
}