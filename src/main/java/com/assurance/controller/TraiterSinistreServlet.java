package com.assurance.controller;

import com.assurance.dao.SinistreDao;
import com.assurance.model.Commentaire;
import com.assurance.model.Role;
import com.assurance.model.Sinistre;
import com.assurance.model.StatutSinistre;
import com.assurance.model.Utilisateur;
import com.assurance.util.SessionKeys;
import com.assurance.util.Validateur;
import com.assurance.service.BusinessException;
import com.assurance.service.SinistreService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Servlet réservée à l'agent d'assurance pour traiter un sinistre :
 */
@WebServlet("/agent/sinistre")
public class TraiterSinistreServlet extends HttpServlet {

    private static final String VUE = "/WEB-INF/views/agent/sinistre-detail.jsp";

    private final SinistreDao sinistreDao = new SinistreDao();
    private final SinistreService sinistreService = new SinistreService();

    // ===================== GET =====================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Utilisateur agent = agentConnecte(req, resp);
        if (agent == null) return;

        Validateur v = new Validateur();
        Long id = v.id(req.getParameter("id"), "L'identifiant du sinistre");
        if (!v.estValide()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, v.messages());
            return;
        }

        Sinistre s = sinistreDao.findByIdAvecCommentaires(id);
        if (s == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Sinistre introuvable");
            return;
        }
        req.setAttribute("sinistre", s);
        req.setAttribute("statuts", StatutSinistre.values());
        req.setAttribute("transitions", SinistreService.transitionsAutorisees());
        req.setAttribute("agent", agent);
        req.getRequestDispatcher(VUE).forward(req, resp);
    }

    // ===================== POST =====================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Utilisateur agent = agentConnecte(req, resp);
        if (agent == null) return;

        Validateur v = new Validateur();
        Long sinistreId = v.id(req.getParameter("sinistreId"), "L'identifiant du sinistre");
        String action   = v.parmi(req.getParameter("action"), "L'action",
                                  "ajouterCommentaire", "changerStatut", "indemniser");
        if (sinistreId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, v.messages());
            return;
        }

        try {
            if (action == null) {
                req.getSession().setAttribute("erreur", v.messages());
            } else switch (action) {
                case "ajouterCommentaire" -> ajouterCommentaire(req, agent, sinistreId);
                case "changerStatut"      -> changerStatut(req, sinistreId);
                case "indemniser"         -> indemniser(req, sinistreId);
            }
        } catch (BusinessException | IllegalArgumentException | IllegalStateException e) {
            req.getSession().setAttribute("erreur", e.getMessage());
        } catch (Exception e) {
            log("Erreur lors du traitement du sinistre " + sinistreId, e);
            req.getSession().setAttribute("erreur", "Erreur technique, merci de réessayer.");
        }
        resp.sendRedirect(req.getContextPath() + "/agent/sinistre?id=" + sinistreId);   // PRG
    }

    // ===================== ACTIONS =====================

    private void ajouterCommentaire(HttpServletRequest req, Utilisateur agent, Long sinistreId) {
        Validateur v = new Validateur();
        String texte = v.texte(req.getParameter("commentaire"), "Le commentaire", 1, 2000, true);
        if (!v.estValide()) throw new IllegalArgumentException(v.messages());

        Commentaire c = new Commentaire();
        c.setTexte(texte);
        c.setDateCreation(LocalDateTime.now());
        c.setAuteur(agent);
        sinistreDao.ajouterCommentaire(sinistreId, c);
        req.getSession().setAttribute("message", "Commentaire ajouté.");
    }

    private void changerStatut(HttpServletRequest req, Long sinistreId) {
        Validateur v = new Validateur();
        StatutSinistre statut = v.choix(StatutSinistre.class, req.getParameter("statut"), "Le statut");
        if (!v.estValide()) throw new IllegalArgumentException(v.messages());

        sinistreService.changerStatut(sinistreId, statut);   // transitions vérifiées par le service
        req.getSession().setAttribute("message", "Statut mis à jour : " + statut);
    }

    private void indemniser(HttpServletRequest req, Long sinistreId) {
        Validateur v = new Validateur();
        BigDecimal montant = v.montant(req.getParameter("montantIndemnisation"),
                                       "Le montant d'indemnisation", true, true);
        if (!v.estValide()) throw new IllegalArgumentException(v.messages());

        sinistreService.indemniser(sinistreId, montant);
        req.getSession().setAttribute("message", "Sinistre indemnisé (" + montant + " €).");
    }

    // ===================== UTILITAIRES =====================

    /** Récupère l'agent connecté, ou redirige vers /login et renvoie null. */
    private Utilisateur agentConnecte(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        Utilisateur u = session == null ? null
                                        : (Utilisateur) session.getAttribute(SessionKeys.UTILISATEUR);
        if (u == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        if (u.getRole() != Role.AGENT && u.getRole() != Role.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès réservé aux agents");
            return null;
        }
        return u;
    }
}