package com.assurance.controller.agent;

import com.assurance.dao.GarantieDao;
import com.assurance.dao.TypeAssuranceDao;
import com.assurance.dao.UtilisateurDao;
import com.assurance.model.*;
import com.assurance.service.BusinessException;
import com.assurance.service.ContratService;
import com.assurance.util.FormUtil;
import com.assurance.util.Validateur;

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

            // id caché : null = création (ce n'est pas une erreur)
            Long id = FormUtil.parseLong(req.getParameter("id"));

            // ---------- validation ----------
            Validateur v = new Validateur();
            LocalDate dateDeb = v.date(req.getParameter("dateDebut"), "La date de début", true);
            LocalDate dateFin = v.date(req.getParameter("dateFin"), "La date de fin", false);
            v.periode(dateDeb, dateFin);
            BigDecimal prime  = v.montant(req.getParameter("prime"), "La prime", true, true);
            Long typeId       = v.id(req.getParameter("typeId"), "Le type d'assurance");
            Long clientId     = (id == null) ? v.id(req.getParameter("clientId"), "Le client") : null;
            String numero     = (id == null)
                    ? v.texte(req.getParameter("numeroContrat"), "Le numéro de contrat", 3, 30, false)
                    : null;

            Set<Long> selIds = new HashSet<>();
            List<Garantie> garanties = new ArrayList<>();
            String[] garIds = req.getParameterValues("garantieIds");
            if (garIds != null) {
                for (String s : garIds) {
                    Long gid = FormUtil.parseLong(s);
                    Garantie g = gid == null ? null : garantieDao.findById(gid);
                    if (g == null) { v.ajouter("Garantie inconnue : " + s); continue; }
                    garanties.add(g);
                    selIds.add(gid);
                }
            }
            req.setAttribute("selIds", selIds);

            TypeAssurance type = typeId == null ? null : typeDao.findById(typeId);
            if (typeId != null && type == null) v.ajouter("Type d'assurance inconnu.");

            if (!v.estValide()) { echec(req, resp, id, v.getErreurs()); return; }

            try {
                Contrat c;
                if (id == null) {
                    Utilisateur client = utilisateurDao.findById(clientId);
                    if (client == null || client.getRole() != Role.CLIENT) {
                        throw new BusinessException("Client inconnu.");
                    }
                    c = new Contrat();
                    c.setNumeroContrat(numero);
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
                        + "&msg=" + FormUtil.enc("Contrat enregistré"));
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