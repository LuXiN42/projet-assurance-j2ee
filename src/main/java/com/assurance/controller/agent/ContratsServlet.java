package com.assurance.controller.agent;

import com.assurance.dao.ContratDao;
import com.assurance.dao.TypeAssuranceDao;
import com.assurance.dao.UtilisateurDao;
import com.assurance.model.Contrat;
import com.assurance.model.StatutContrat;
import com.assurance.model.Utilisateur;
import com.assurance.service.ContratService;
import com.assurance.util.FormUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/agent/contrats")
public class ContratsServlet extends HttpServlet {

    private final ContratService service = new ContratService();
    private final TypeAssuranceDao typeDao = new TypeAssuranceDao();
    private final ContratDao contratDao = new ContratDao();
    private final UtilisateurDao utilisateurDao = new UtilisateurDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        service.mettreAJourContratsExpires();

        List<Contrat> contrats;
        Long clientId = FormUtil.parseLong(req.getParameter("clientId"));

        if (clientId != null) {
            // Filtre par client (lien « Voir » de la liste des clients)
            Utilisateur client = utilisateurDao.findById(clientId);
            if (client == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Client introuvable");
                return;
            }
            contrats = contratDao.findByClient(clientId);
            req.setAttribute("clientFiltre", client);
        } else {
            String numero    = req.getParameter("numero");
            String nomClient = req.getParameter("nomClient");
            Long typeId      = FormUtil.parseLong(req.getParameter("typeId"));
            StatutContrat st = FormUtil.parseEnum(StatutContrat.class, req.getParameter("statut"));
            contrats = service.rechercher(numero, nomClient, typeId, st);
        }

        req.setAttribute("contrats", contrats);
        req.setAttribute("types", typeDao.findAll());
        req.setAttribute("statuts", StatutContrat.values());
        req.setAttribute("message", req.getParameter("msg"));
        req.getRequestDispatcher("/WEB-INF/views/agent/contrats.jsp").forward(req, resp);
    }
}