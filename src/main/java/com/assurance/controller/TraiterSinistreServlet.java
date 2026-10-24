package com.assurance.controller;

import com.assurance.dao.SinistreDao;
import com.assurance.model.Commentaire;
import com.assurance.model.Role;
import com.assurance.model.Sinistre;
import com.assurance.model.StatutSinistre;
import com.assurance.model.Utilisateur;
import com.assurance.util.SessionKeys;
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

        Long id = parseId(req.getParameter("id"));
        if (id == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identifiant de sinistre manquant");
            return;
        }

        // findByIdAvecCommentaires : join fetch des commentaires + auteur
        // + contrat + client → évite LazyInitializationException dans la JSP.
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

        String action = req.getParameter("action");
        Long sinistreId = parseId(req.getParameter("sinistreId"));
        if (sinistreId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identifiant de sinistre manquant");
            return;
        }

        try {
            if ("ajouterCommentaire".equals(action)) {
                ajouterCommentaire(req, agent, sinistreId);
            } else if ("changerStatut".equals(action)) {
                changerStatut(req, sinistreId);
            } else if ("indemniser".equals(action)) {
                indemniser(req, sinistreId);
            } else {
                req.getSession().setAttribute("erreur", "Action inconnue : " + action);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            req.getSession().setAttribute("erreur", e.getMessage());
        } catch (Exception e) {
            log("Erreur lors du traitement du sinistre " + sinistreId, e);
            req.getSession().setAttribute("erreur", "Erreur technique, merci de réessayer.");
        }

        // PRG
        resp.sendRedirect(req.getContextPath() + "/agent/sinistre?id=" + sinistreId);
    }

    // ===================== ACTIONS =====================

    private void ajouterCommentaire(HttpServletRequest req, Utilisateur agent, Long sinistreId) {
        String texte = trim(req.getParameter("commentaire"));
        if (texte.isEmpty()) {
            throw new IllegalArgumentException("Le commentaire ne peut pas être vide");
        }
        if (texte.length() > 2000) {
            throw new IllegalArgumentException("Le commentaire dépasse 2000 caractères");
        }

        Commentaire c = new Commentaire();
        c.setTexte(texte);
        c.setDateCreation(LocalDateTime.now());
        c.setAuteur(agent);
        // Pas de save séparé : ajouterCommentaire charge le sinistre,
        // pose le lien bidirectionnel et profite de cascade = ALL
        // (Sinistre.commentaires) pour insérer en une seule transaction.
        sinistreDao.ajouterCommentaire(sinistreId, c);
        req.getSession().setAttribute("message", "Commentaire ajouté.");
    }

    private void changerStatut(HttpServletRequest req, Long sinistreId) {
        StatutSinistre nouveauStatut;
        try {
            nouveauStatut = StatutSinistre.valueOf(req.getParameter("statut"));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BusinessException("Statut invalide");
        }
        // La validation des transitions + montant est faite par le service.
        sinistreService.changerStatut(sinistreId, nouveauStatut);
        req.getSession().setAttribute("message", "Statut mis à jour : " + nouveauStatut);
    }

    private void indemniser(HttpServletRequest req, Long sinistreId) {
        BigDecimal montant = parseMontant(req.getParameter("montantIndemnisation"), true);
        sinistreDao.indemniser(sinistreId, montant);
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
        // NOTE : Role.java déclare AGENT_ASSURANCE ; les autres servlets
        // utilisent Role.AGENT. On accepte les deux pour ne pas casser
        // l'authentification si le code est harmonisé ultérieurement.
        if (u.getRole() != Role.AGENT && u.getRole() != Role.ADMIN
                && !"AGENT_ASSURANCE".equals(u.getRole().name())
                && !"ADMINISTRATEUR".equals(u.getRole().name())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès réservé aux agents");
            return null;
        }
        return u;
    }

    private Long parseId(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try { return Long.parseLong(raw.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    /**
     * Parse un montant BigDecimal. Si {@code obligatoire}, un montant null
     * ou ≤ 0 déclenche une exception. La virgule est tolérée comme
     * séparateur décimal.
     */
    private BigDecimal parseMontant(String raw, boolean obligatoire) {
        String txt = trim(raw);
        if (txt.isEmpty()) {
            if (obligatoire) {
                throw new IllegalArgumentException("Le montant est obligatoire");
            }
            return null;
        }
        try {
            BigDecimal v = new BigDecimal(txt.replace(',', '.'));
            if (v.signum() < 0) {
                throw new IllegalArgumentException("Le montant ne peut pas être négatif");
            }
            return v;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Montant invalide");
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}