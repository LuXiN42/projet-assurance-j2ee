package com.assurance.controller.agent;

import com.assurance.dao.GarantieDao;
import com.assurance.dao.TypeAssuranceDao;
import com.assurance.dao.UtilisateurDao;
import com.assurance.model.*;
import com.assurance.service.BusinessException;
import com.assurance.service.ContratService;
import com.assurance.util.FormUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@WebServlet("/agent/contrats/form")
public class ContratFormServlet extends HttpServlet {

    private static final String VUE = "/WEB-INF/views/agent/contrat-form.jsp";

    private final ContratService service = new ContratService();
    private final UtilisateurDao utilisateurDao = new UtilisateurDao();
    private final TypeAssuranceDao typeDao = new TypeAssuranceDao();
    private final GarantieDao garantieDao = new GarantieDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long id = FormUtil.parseLong(req.getParameter("id"));
        Contrat contrat = null;
        Set<Long> selIds = new HashSet<>();
        if (id != null) {
            try {
                contrat = service.detail(id);
                for (Garantie g : contrat.getGaranties()) selIds.add(g.getId());
            } catch (BusinessException e) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
                return;
            }
        }
        req.setAttribute("contrat", contrat);
        req.setAttribute("selIds", selIds);
        afficher(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        Long id            = FormUtil.parseLong(req.getParameter("id"));
        LocalDate dateDeb  = FormUtil.parseDate(req.getParameter("dateDebut"));
        LocalDate dateFin  = FormUtil.parseDate(req.getParameter("dateFin"));
        BigDecimal prime   = FormUtil.parseBigDecimal(req.getParameter("prime"));
        Long clientId      = FormUtil.parseLong(req.getParameter("clientId"));
        Long typeId        = FormUtil.parseLong(req.getParameter("typeId"));
        String[] garIds    = req.getParameterValues("garantieIds");

        // ---- Validation côté servlet (exigence section 4 du sujet) ----
        List<String> erreurs = new ArrayList<>();
        if (dateDeb == null) erreurs.add("Date de début manquante ou invalide (format AAAA-MM-JJ).");
        if (!FormUtil.isBlank(req.getParameter("dateFin")) && dateFin == null)
            erreurs.add("Date de fin invalide.");
        if (prime == null) erreurs.add("Prime manquante ou invalide.");
        if (typeId == null) erreurs.add("Type d'assurance manquant.");
        if (id == null && clientId == null) erreurs.add("Client manquant.");

        Set<Long> selIds = new HashSet<>();
        List<Garantie> garanties = new ArrayList<>();
        if (garIds != null) {
            for (String s : garIds) {
                Long gid = FormUtil.parseLong(s);
                Garantie g = gid == null ? null : garantieDao.findById(gid);
                if (g == null) { erreurs.add("Garantie inconnue : " + s); continue; }
                garanties.add(g);
                selIds.add(gid);
            }
        }
        req.setAttribute("selIds", selIds);

        TypeAssurance type = typeId == null ? null : typeDao.findById(typeId);
        if (typeId != null && type == null) erreurs.add("Type d'assurance inconnu.");

        if (!erreurs.isEmpty()) { echec(req, resp, id, erreurs); return; }

        try {
            Contrat c;
            if (id == null) {
                Utilisateur client = utilisateurDao.findById(clientId);
                if (client == null || client.getRole() != Role.CLIENT) {
                    throw new BusinessException("Client inconnu.");
                }
                c = new Contrat();
                c.setNumeroContrat(req.getParameter("numeroContrat"));
                c.setDateDebut(dateDeb);
                c.setDateFin(dateFin);
                c.setPrime(prime);
                c.setClient(client);
                c.setTypeAssurance(type);
                c.setGaranties(garanties);
                c = service.souscrire(c);
            } else {
                c = service.modifier(id, dateDeb, dateFin, prime, type, garanties);
            }
            resp.sendRedirect(req.getContextPath() + "/agent/contrats/detail?id=" + c.getId()
                    + "&msg=Contrat+enregistr%C3%A9");
        } catch (BusinessException e) {
            echec(req, resp, id, List.of(e.getMessage()));
        }
    }

    private void echec(HttpServletRequest req, HttpServletResponse resp, Long id, List<String> erreurs)
            throws ServletException, IOException {
        req.setAttribute("erreurs", erreurs);
        req.setAttribute("contrat", id == null ? null : service.detail(id));
        afficher(req, resp);
    }

    private void afficher(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("clients", utilisateurDao.findByRole(Role.CLIENT));
        req.setAttribute("types", typeDao.findAll());
        req.setAttribute("garanties", garantieDao.findAll());
        req.getRequestDispatcher(VUE).forward(req, resp);
    }
}