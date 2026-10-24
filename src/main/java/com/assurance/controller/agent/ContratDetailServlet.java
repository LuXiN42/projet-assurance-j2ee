package com.assurance.controller.agent;

import com.assurance.service.BusinessException;
import com.assurance.service.ContratService;
import com.assurance.util.FormUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/agent/contrats/detail")
public class ContratDetailServlet extends HttpServlet {

    private final ContratService service = new ContratService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("contrat", service.detail(FormUtil.parseLong(req.getParameter("id"))));
            req.setAttribute("message", req.getParameter("msg"));
            req.setAttribute("erreur", req.getParameter("err"));
            req.getRequestDispatcher("/WEB-INF/views/agent/contrat-detail.jsp").forward(req, resp);
        } catch (BusinessException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }
}