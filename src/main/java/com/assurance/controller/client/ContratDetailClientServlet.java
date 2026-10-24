package com.assurance.controller.client;

import com.assurance.model.Utilisateur;
import com.assurance.service.BusinessException;
import com.assurance.service.ContratService;
import com.assurance.util.FormUtil;
import com.assurance.util.SessionKeys;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/client/contrats/detail")
public class ContratDetailClientServlet extends HttpServlet {

    private final ContratService service = new ContratService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Utilisateur u = (Utilisateur) req.getSession().getAttribute(SessionKeys.UTILISATEUR);
        try {
            req.setAttribute("contrat",
                    service.detailPourClient(FormUtil.parseLong(req.getParameter("id")), u.getId()));
            req.getRequestDispatcher("/WEB-INF/views/client/contrat-detail.jsp").forward(req, resp);
        } catch (BusinessException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }
}