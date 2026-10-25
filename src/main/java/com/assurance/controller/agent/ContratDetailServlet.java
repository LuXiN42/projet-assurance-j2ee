package com.assurance.controller.agent;

import com.assurance.dao.GarantieDao;
import com.assurance.model.Contrat;
import com.assurance.model.Garantie;
import com.assurance.service.BusinessException;
import com.assurance.service.ContratService;
import com.assurance.util.FormUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet("/agent/contrats/detail")
public class ContratDetailServlet extends HttpServlet {

    private final ContratService service = new ContratService();
    private final GarantieDao garantieDao = new GarantieDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Contrat contrat = service.detail(FormUtil.parseLong(req.getParameter("id")));
            req.setAttribute("contrat", contrat);

            // Garanties du type d'assurance qui ne sont pas encore dans le contrat
            Set<Long> deja = new HashSet<>();
            for (Garantie g : contrat.getGaranties()) {
                deja.add(g.getId());
            }
            List<Garantie> dispo = new ArrayList<>();
            for (Garantie g : garantieDao.findByTypeAssurance(contrat.getTypeAssurance().getId())) {
                if (!deja.contains(g.getId())) {
                    dispo.add(g);
                }
            }
            req.setAttribute("garantiesDispo", dispo);

            req.setAttribute("message", req.getParameter("msg"));
            req.setAttribute("erreur", req.getParameter("err"));
            req.getRequestDispatcher("/WEB-INF/views/agent/contrat-detail.jsp").forward(req, resp);
        } catch (BusinessException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }
}