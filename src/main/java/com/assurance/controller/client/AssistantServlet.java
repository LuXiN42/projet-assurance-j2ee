package com.assurance.controller.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.assurance.dao.ContratDao;
import com.assurance.dao.SinistreDao;
import com.assurance.model.Contrat;
import com.assurance.model.Garantie;
import com.assurance.model.Sinistre;
import com.assurance.model.Utilisateur;
import com.assurance.service.AssistantIAService;
import com.assurance.service.BusinessException;
import com.assurance.util.FormUtil;
import com.assurance.util.SessionKeys;
import com.assurance.util.Validateur;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/client/assistant")
public class AssistantServlet extends HttpServlet {

    private static final String HISTORIQUE = "historiqueIA";
    // on garde seulement les 20 derniers messages pour ne pas envoyer trop de texte
    private static final int MAX_MESSAGES = 20;

    private AssistantIAService assistant = new AssistantIAService();
    private ContratDao contratDao = new ContratDao();
    private SinistreDao sinistreDao = new SinistreDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // la bulle Bibi récupère la conversation en JSON
        if ("json".equals(request.getParameter("format"))) {
            JsonArray messages = new JsonArray();
            for (String[] m : getHistorique(request.getSession())) {
                JsonObject o = new JsonObject();
                o.addProperty("role", m[0]);
                o.addProperty("texte", m[1]);
                messages.add(o);
            }
            JsonObject json = new JsonObject();
            json.add("messages", messages);
            envoyerJson(response, json);
            return;
        }

        request.setAttribute("historique", getHistorique(request.getSession()));
        request.setAttribute("erreur", request.getParameter("err"));
        request.getRequestDispatcher("/WEB-INF/views/client/assistant.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String url = request.getContextPath() + "/client/assistant";
        boolean json = "json".equals(request.getParameter("format"));
        JsonObject resultat = new JsonObject();

        if ("effacer".equals(request.getParameter("action"))) {
            session.removeAttribute(HISTORIQUE);
            if (json) {
                envoyerJson(response, resultat);
            } else {
                response.sendRedirect(url);
            }
            return;
        }

        Validateur v = new Validateur();
        String question = v.texte(request.getParameter("question"), "La question", 2, 500, true);
        if (!v.estValide()) {
            if (json) {
                resultat.addProperty("erreur", v.messages());
                envoyerJson(response, resultat);
            } else {
                response.sendRedirect(url + "?err=" + FormUtil.enc(v.messages()));
            }
            return;
        }

        Utilisateur client = (Utilisateur) session.getAttribute(SessionKeys.UTILISATEUR);
        List<String[]> historique = getHistorique(session);
        historique.add(new String[] { "user", question });

        String reponse;
        try {
            reponse = assistant.repondre(consignes(client), historique);
            historique.add(new String[] { "model", reponse });
        } catch (BusinessException e) {
            // la question n'a pas eu de réponse : on la retire de l'historique
            historique.remove(historique.size() - 1);
            if (json) {
                resultat.addProperty("erreur", e.getMessage());
                envoyerJson(response, resultat);
            } else {
                response.sendRedirect(url + "?err=" + FormUtil.enc(e.getMessage()));
            }
            return;
        }

        while (historique.size() > MAX_MESSAGES) {
            historique.remove(0);
        }

        if (json) {
            resultat.addProperty("reponse", reponse);
            envoyerJson(response, resultat);
        } else {
            response.sendRedirect(url);
        }
    }

    private void envoyerJson(HttpServletResponse response, JsonObject json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json.toString());
    }

    @SuppressWarnings("unchecked")
    private List<String[]> getHistorique(HttpSession session) {
        List<String[]> historique = (List<String[]>) session.getAttribute(HISTORIQUE);
        if (historique == null) {
            historique = new ArrayList<>();
            session.setAttribute(HISTORIQUE, historique);
        }
        return historique;
    }

    // texte envoyé à l'IA pour lui expliquer son rôle et lui donner les infos du client
    private String consignes(Utilisateur client) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tu es Bibi, l'assistant virtuel de B'Care, une compagnie d'assurance. ");
        sb.append("Tu réponds en français, avec des phrases courtes et simples, sans mise en forme (pas de gras ni de listes avec des étoiles). ");
        sb.append("Tu aides le client à comprendre ses contrats, ses garanties, ses sinistres et ses paiements, ");
        sb.append("et tu peux lui expliquer comment déclarer un sinistre depuis son espace. ");
        sb.append("N'invente aucune information : si tu ne sais pas, conseille-lui de contacter un agent. ");
        sb.append("Si la question n'a rien à voir avec l'assurance, dis-le poliment.\n\n");

        sb.append("Client : ").append(client.getPrenom()).append(" ").append(client.getNom()).append("\n");

        List<Contrat> contrats = contratDao.findByClient(client.getId());
        sb.append("\nSes contrats :\n");
        if (contrats.isEmpty()) {
            sb.append("- aucun contrat\n");
        }
        for (Contrat c : contrats) {
            sb.append("- n° ").append(c.getNumeroContrat())
              .append(", ").append(c.getTypeAssurance().getNom())
              .append(", statut ").append(c.getStatut())
              .append(", prime ").append(c.getPrime()).append(" €")
              .append(", du ").append(c.getDateDebut())
              .append(" au ").append(c.getDateFin() == null ? "(pas de fin)" : c.getDateFin());
            List<String> noms = new ArrayList<>();
            for (Garantie g : c.getGaranties()) {
                noms.add(g.getNom());
            }
            sb.append(", garanties : ").append(noms.isEmpty() ? "aucune" : String.join(", ", noms)).append("\n");
        }

        List<Sinistre> sinistres = sinistreDao.findByClient(client.getId());
        sb.append("\nSes sinistres :\n");
        if (sinistres.isEmpty()) {
            sb.append("- aucun sinistre\n");
        }
        for (Sinistre s : sinistres) {
            sb.append("- n° ").append(s.getNumeroSinistre())
              .append(" du ").append(s.getDate())
              .append(", contrat ").append(s.getContrat().getNumeroContrat())
              .append(", statut ").append(s.getStatut());
            if (s.getTypeSinistre() != null) {
                sb.append(", type ").append(s.getTypeSinistre());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}