package com.assurance.controller.admin;

import com.assurance.dao.StatistiquesDao;
import com.assurance.util.JdbcUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Tableau de bord statistiques (T28) — version ADMIN.
 */
@WebServlet("/admin/statistiques")
public class StatistiquesAdminServlet extends HttpServlet {

    private static final String VUE = "/WEB-INF/views/admin/statistiques.jsp";

    private final StatistiquesDao dao = new StatistiquesDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        // -------- KPI : Clients --------
        long totalClients   = dao.countClients();
        long newClients30j  = dao.countNewClients(30);
        List<Map<String, Object>> clientsParType = dao.clientsParTypeAssurance();

        // -------- KPI : Contrats --------
        long cActifs   = dao.countContratsParStatut("ACTIF");
        long cAttente  = dao.countContratsParStatut("EN_ATTENTE");
        long cSuspendu = dao.countContratsParStatut("SUSPENDU");
        long cExpires  = dao.countContratsParStatut("EXPIRE");
        long cResilies = dao.countContratsParStatut("RESILIE");
        Map<String, Long> evolCtr = dao.evolutionContrats12();
        BigDecimal primeMoyenne = primeMoyenneContrats();

        // -------- KPI : Finances --------
        BigDecimal ca     = dao.caEncaisse();
        BigDecimal indem  = dao.totalIndemnisations();
        BigDecimal primes = dao.primesContratsActifs();
        Map<String, BigDecimal> evolPaie = dao.evolutionPaiements12();
        Map<String, BigDecimal> evolIndem = evolutionIndemnisations12();
        long nbPaiementsEnAttente = countPaiementsByStatut("EN_ATTENTE");

        // -------- KPI : Sinistres --------
        List<Map<String, Object>> repSinBrut = dao.repartitionSinistres();
        List<Map<String, Object>> sinistresParStatut = normalizeSinistres(repSinBrut);
        List<Map<String, Object>> topContratsSinistres = topContratsSinistres();

        // -------- KPI : Admin / RH --------
        long nbAgents        = dao.countAgents();
        long nbAdmins        = dao.countAdmins();
        BigDecimal tauxSin   = dao.tauxSinistralite();
        BigDecimal panierMoy = dao.panierMoyen();

        // -------- Attributs numériques consommés par la JSP --------
        req.setAttribute("totalClients",    totalClients);
        req.setAttribute("newClients30j",   newClients30j);
        req.setAttribute("clientsParType",  clientsParType);

        req.setAttribute("cActifs",   cActifs);
        req.setAttribute("cAttente",  cAttente);
        req.setAttribute("cSuspendu", cSuspendu);
        req.setAttribute("cExpires",  cExpires);
        req.setAttribute("cResilies", cResilies);
        req.setAttribute("evolCtr",   evolCtr);
        req.setAttribute("primeMoyenne", primeMoyenne);

        req.setAttribute("ca",            ca);
        req.setAttribute("indemnisations", indem);
        req.setAttribute("primes",        primes);
        req.setAttribute("evolPaie",      evolPaie);
        req.setAttribute("nbPaiementsEnAttente", nbPaiementsEnAttente);

        req.setAttribute("sinistresParStatut",   sinistresParStatut);
        req.setAttribute("topContratsSinistres", topContratsSinistres);

        req.setAttribute("nbAgents",       nbAgents);
        req.setAttribute("nbAdmins",       nbAdmins);
        req.setAttribute("tauxSinistralite", tauxSin);
        req.setAttribute("panierMoyen",    panierMoy);

        // -------- Attributs JSON (Chart.js) --------
        req.setAttribute("clientsParTypeLabelsJson", toJsonArrayRows(clientsParType, "type"));
        req.setAttribute("clientsParTypeDataJson",   toJsonArrayRowsLong(clientsParType, "nb_clients"));

        req.setAttribute("evolContratsLabelsJson", toJsonArrayKeys(evolCtr));
        req.setAttribute("evolContratsDataJson",   toJsonArrayValuesLong(evolCtr));

        req.setAttribute("evolFinLabelsJson",       toJsonArrayKeys(evolPaie));
        req.setAttribute("evolFinPaiementsJson",    toJsonArrayValuesBigDecimal(evolPaie));
        req.setAttribute("evolFinIndemJson",        toJsonArrayValuesBigDecimal(evolIndem));

        req.setAttribute("sinistresStatutLabelsJson", toJsonArrayRows(sinistresParStatut, "statut"));
        req.setAttribute("sinistresStatutDataJson",   toJsonArrayRowsLong(sinistresParStatut, "total"));

        req.getRequestDispatcher(VUE).forward(req, resp);
    }

    // ========================================================================
    //                         Calculs JDBC complémentaires
    // ========================================================================

    /** Prime moyenne sur tous les contrats. */
    private BigDecimal primeMoyenneContrats() {
        final String sql = "SELECT COALESCE(AVG(prime),0) FROM contrats";
        try (Connection cn = JdbcUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                BigDecimal v = rs.getBigDecimal(1);
                return v == null ? BigDecimal.ZERO : v.setScale(2, RoundingMode.HALF_UP);
            }
        } catch (Exception ignored) {}
        return BigDecimal.ZERO;
    }

    /** Nombre de paiements pour un statut donné. */
    private long countPaiementsByStatut(String statut) {
        final String sql = "SELECT COUNT(*) FROM paiements WHERE statut = ?";
        try (Connection cn = JdbcUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, statut);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        } catch (Exception e) {
            return 0L;
        }
    }

    /** Évolution mensuelle des indemnisations (statut = 'INDEMNISE'). */
    private Map<String, BigDecimal> evolutionIndemnisations12() {
        Map<String, BigDecimal> out = new TreeMap<>();
        java.time.LocalDate today = java.time.LocalDate.now();
        for (int i = 11; i >= 0; i--) {
            java.time.LocalDate d = today.minusMonths(i);
            out.put(String.format("%04d-%02d", d.getYear(), d.getMonthValue()), BigDecimal.ZERO);
        }
        final String sql =
                "SELECT DATE_FORMAT(`date`, '%Y-%m') AS mois, " +
                "       COALESCE(SUM(montant_indemnisation),0) AS total " +
                "  FROM sinistres " +
                " WHERE `date` >= ? AND statut = 'INDEMNISE' " +
                " GROUP BY DATE_FORMAT(`date`, '%Y-%m') " +
                " ORDER BY 1";
        try (Connection cn = JdbcUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(today.minusMonths(11).withDayOfMonth(1)));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String m = rs.getString("mois");
                    if (out.containsKey(m)) {
                        out.put(m, rs.getBigDecimal("total"));
                    }
                }
            }
        } catch (Exception ignored) {}
        return out;
    }

    /**
     * Réorganise les lignes du DAO (clé = "statut", valeur = "nb") sous la forme
     * attendue par la JSP : clé = "statut", valeur = "total".
     */
    private List<Map<String, Object>> normalizeSinistres(List<Map<String, Object>> rows) {
        List<Map<String, Object>> out = new ArrayList<>(rows.size());
        for (Map<String, Object> r : rows) {
            Map<String, Object> n = new LinkedHashMap<>();
            n.put("statut", r.get("statut"));
            Object nb = r.get("nb");
            n.put("total", nb == null ? 0L : ((Number) nb).longValue());
            out.add(n);
        }
        return out;
    }

    /** Top 5 contrats les plus sinistrés. */
    private List<Map<String, Object>> topContratsSinistres() {
        final String sql =
                "SELECT contrat_id, COUNT(*) AS total " +
                "  FROM sinistres " +
                " GROUP BY contrat_id " +
                " ORDER BY total DESC, contrat_id ASC " +
                " LIMIT 5";
        List<Map<String, Object>> out = new ArrayList<>();
        try (Connection cn = JdbcUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> r = new LinkedHashMap<>();
                r.put("contratId", rs.getLong("contrat_id"));
                r.put("total",     rs.getLong("total"));
                out.add(r);
            }
        } catch (Exception ignored) {}
        return out;
    }

    // ========================================================================
    //                        Sérialiseurs JSON minimalistes
    // ========================================================================

    private static String toJsonArrayKeys(Map<String, ?> m) {
        return toJsonArray(m.keySet());
    }

    private static String toJsonArray(Collection<String> c) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (String s : c) {
            if (!first) sb.append(",");
            sb.append("\"").append(escape(s)).append("\"");
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    private static String toJsonArrayValuesLong(Map<String, Long> m) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Long v : m.values()) {
            if (!first) sb.append(",");
            sb.append(v == null ? 0 : v);
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    private static String toJsonArrayValuesBigDecimal(Map<String, BigDecimal> m) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (BigDecimal v : m.values()) {
            if (!first) sb.append(",");
            if (v == null) sb.append(0);
            else sb.append(v.setScale(2, RoundingMode.HALF_UP).toPlainString());
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    private static String toJsonArrayRows(List<Map<String, Object>> rows, String key) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Map<String, Object> r : rows) {
            Object v = r.get(key);
            if (!first) sb.append(",");
            sb.append("\"").append(escape(v == null ? "" : v.toString())).append("\"");
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    private static String toJsonArrayRowsLong(List<Map<String, Object>> rows, String key) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Map<String, Object> r : rows) {
            Object v = r.get(key);
            long n = 0;
            if (v instanceof Number) n = ((Number) v).longValue();
            if (!first) sb.append(",");
            sb.append(n);
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
