package com.assurance.dao;

import com.assurance.util.JdbcUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * DAO statistiques en JDBC pur.
 */
public class StatistiquesDao {

    // ---------- clients ----------

    public long countClients() {
        return scalarLong(
                "SELECT COUNT(*) FROM utilisateurs WHERE role = 'CLIENT'");
    }

    public long countNewClients(int days) {
        return scalarLong(
                "SELECT COUNT(*) FROM utilisateurs " +
                "WHERE role = 'CLIENT' AND date_inscription >= ?",
                Date.valueOf(LocalDate.now().minusDays(days)));
    }

    /** Compte les clients par mois sur les 12 derniers mois (clé = "YYYY-MM"). */
    public Map<String, Long> clientsParMois12() {
        Map<String, Long> out = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 11; i >= 0; i--) {
            LocalDate d = today.minusMonths(i);
            out.put(String.format("%04d-%02d", d.getYear(), d.getMonthValue()), 0L);
        }
        String sql =
                "SELECT DATE_FORMAT(date_inscription, '%Y-%m') AS mois, COUNT(*) AS n " +
                "  FROM utilisateurs " +
                " WHERE role = 'CLIENT' " +
                "   AND date_inscription >= ? " +
                " GROUP BY DATE_FORMAT(date_inscription, '%Y-%m') " +
                " ORDER BY 1";
        try (Connection cn = JdbcUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(today.minusMonths(11).withDayOfMonth(1)));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String mois = rs.getString("mois");
                    if (out.containsKey(mois)) {
                        out.put(mois, rs.getLong("n"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("clientsParMois12", e);
        }
        return out;
    }

    /** Clients regroupés par type d'assurance souscrit. */
    public List<Map<String, Object>> clientsParTypeAssurance() {
        return queryRows(
                "SELECT ta.nom AS type, COUNT(DISTINCT c.client_id) AS nb_clients " +
                "  FROM types_assurance ta " +
                "  LEFT JOIN contrats c ON c.type_assurance_id = ta.id " +
                " GROUP BY ta.id, ta.nom " +
                " ORDER BY nb_clients DESC, ta.nom ASC");
    }

    // ---------- contrats ----------

    public long countContratsParStatut(String statut) {
        return scalarLong(
                "SELECT COUNT(*) FROM contrats WHERE statut = ?", statut);
    }

    public List<Map<String, Object>> repartitionContrats() {
        return queryRows(
                "SELECT statut, COUNT(*) AS nb " +
                "  FROM contrats " +
                " GROUP BY statut " +
                " ORDER BY nb DESC");
    }

    /** Évolution mensuelle des contrats sur 12 mois (créés = date_debut dans le mois). */
    public Map<String, Long> evolutionContrats12() {
        return evolutionParMois("contrats", "date_debut");
    }

    // ---------- sinistres ----------

    public long countSinistresParStatut(String statut) {
        return scalarLong(
                "SELECT COUNT(*) FROM sinistres WHERE statut = ?", statut);
    }

    public List<Map<String, Object>> repartitionSinistres() {
        return queryRows(
                "SELECT statut, COUNT(*) AS nb " +
                "  FROM sinistres " +
                " GROUP BY statut " +
                " ORDER BY nb DESC");
    }

    public Map<String, Long> evolutionSinistres12() {
        return evolutionParMois("sinistres", "`date`");
    }

    // ---------- finances ----------

    /** CA = somme des paiements statut = 'EFFECTUE' (primes effectivement encaissées). */
    public BigDecimal caEncaisse() {
        BigDecimal v = scalarBigDecimal(
                "SELECT COALESCE(SUM(montant),0) FROM paiements WHERE statut = 'EFFECTUE'");
        return v == null ? BigDecimal.ZERO : v;
    }

    /** Total des indemnisations versées (sinistres statut = 'INDEMNISE'). */
    public BigDecimal totalIndemnisations() {
        BigDecimal v = scalarBigDecimal(
                "SELECT COALESCE(SUM(montant_indemnisation),0) " +
                "  FROM sinistres WHERE statut = 'INDEMNISE'");
        return v == null ? BigDecimal.ZERO : v;
    }

    public BigDecimal primesContratsActifs() {
        BigDecimal v = scalarBigDecimal(
                "SELECT COALESCE(SUM(prime),0) FROM contrats WHERE statut = 'ACTIF'");
        return v == null ? BigDecimal.ZERO : v;
    }

    public Map<String, BigDecimal> evolutionPaiements12() {
        // Fragment sans "WHERE" : evolutionParMoisBigDecimal écrit déjà
        // "WHERE `date` >= ?". Un "AND ..." se combine correctement ici.
        return evolutionParMoisBigDecimal("paiements", "`date`", "montant",
                "AND statut = 'EFFECTUE'");
    }

    // ---------- admin ----------

    public long countAgents() {
        return scalarLong("SELECT COUNT(*) FROM utilisateurs WHERE role = 'AGENT'");
    }

    public long countAdmins() {
        return scalarLong("SELECT COUNT(*) FROM utilisateurs WHERE role = 'ADMIN'");
    }

    /** Taux de sinistralité = nb sinistres / nb contrats (%). */
    public BigDecimal tauxSinistralite() {
        long contrats = scalarLong("SELECT COUNT(*) FROM contrats");
        long sinistres = scalarLong("SELECT COUNT(*) FROM sinistres");
        if (contrats == 0) return BigDecimal.ZERO;
        return new BigDecimal(sinistres * 10000L / contrats).divide(new BigDecimal("100"));
    }

    /** Panier moyen = CA / nb paiements encaissés. */
    public BigDecimal panierMoyen() {
        long n = scalarLong("SELECT COUNT(*) FROM paiements WHERE statut = 'EFFECTUE'");
        if (n == 0) return BigDecimal.ZERO;
        return caEncaisse().divide(new BigDecimal(n), 2, java.math.RoundingMode.HALF_UP);
    }

    public List<Map<String, Object>> topTypesAssurance() {
        return queryRows(
                "SELECT ta.nom AS type, COUNT(c.id) AS nb_contrats, " +
                "       COALESCE(SUM(c.prime),0) AS ca " +
                "  FROM types_assurance ta " +
                "  LEFT JOIN contrats c ON c.type_assurance_id = ta.id " +
                " GROUP BY ta.id, ta.nom " +
                " ORDER BY nb_contrats DESC");
    }

    // ---------- méthodes privées ----------

    private long scalarLong(String sql, Object... params) {
        try (Connection cn = JdbcUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        } catch (SQLException e) {
            throw new RuntimeException("scalarLong: " + sql, e);
        }
    }

    private BigDecimal scalarBigDecimal(String sql, Object... params) {
        try (Connection cn = JdbcUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getBigDecimal(1) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("scalarBigDecimal: " + sql, e);
        }
    }

    private List<Map<String, Object>> queryRows(String sql, Object... params) {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection cn = JdbcUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                int cols = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= cols; i++) {
                        row.put(rs.getMetaData().getColumnLabel(i).toLowerCase(),
                                rs.getObject(i));
                    }
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("queryRows: " + sql, e);
        }
        return rows;
    }

    private Map<String, Long> evolutionParMois(String table, String dateCol) {
        Map<String, Long> out = new TreeMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 11; i >= 0; i--) {
            LocalDate d = today.minusMonths(i);
            out.put(String.format("%04d-%02d", d.getYear(), d.getMonthValue()), 0L);
        }
        String sql =
                "SELECT DATE_FORMAT(" + dateCol + ", '%Y-%m') AS mois, COUNT(*) AS n " +
                "  FROM " + table + " " +
                " WHERE " + dateCol + " >= ? " +
                " GROUP BY DATE_FORMAT(" + dateCol + ", '%Y-%m') " +
                " ORDER BY 1";
        try (Connection cn = JdbcUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(today.minusMonths(11).withDayOfMonth(1)));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String m = rs.getString("mois");
                    if (out.containsKey(m)) out.put(m, rs.getLong("n"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("evolutionParMois " + table, e);
        }
        return out;
    }

    private Map<String, BigDecimal> evolutionParMoisBigDecimal(
            String table, String dateCol, String sumCol, String where) {
        Map<String, BigDecimal> out = new TreeMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 11; i >= 0; i--) {
            LocalDate d = today.minusMonths(i);
            out.put(String.format("%04d-%02d", d.getYear(), d.getMonthValue()), BigDecimal.ZERO);
        }
        // Le fragment `where` est concaténé APRÈS "WHERE dateCol >= ?".
        // Il doit donc être vide, ou commencer par "AND ".
        // On normalise par sécurité : un "WHERE ..." accidentel est converti en "AND ...".
        String extra = (where == null) ? "" : where.trim();
        if (extra.length() == 0) {
            // pas de filtre additionnel
        } else if (extra.regionMatches(true, 0, "WHERE", 0, 5)) {
            extra = "AND " + extra.substring(5).trim();
        } else if (!extra.regionMatches(true, 0, "AND", 0, 3)) {
            // fragment libre -> on force la conjonction
            extra = "AND " + extra;
        }
        String sql =
                "SELECT DATE_FORMAT(" + dateCol + ", '%Y-%m') AS mois, " +
                "       COALESCE(SUM(" + sumCol + "),0) AS total " +
                "  FROM " + table + " " +
                " WHERE " + dateCol + " >= ? " + extra + " " +
                " GROUP BY DATE_FORMAT(" + dateCol + ", '%Y-%m') " +
                " ORDER BY 1";
        try (Connection cn = JdbcUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(today.minusMonths(11).withDayOfMonth(1)));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String m = rs.getString("mois");
                    if (out.containsKey(m)) out.put(m, rs.getBigDecimal("total"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("evolutionParMoisBigDecimal " + table, e);
        }
        return out;
    }

    private static void bind(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object v = params[i];
            if (v == null)        ps.setObject(i + 1, null);
            else if (v instanceof Date) ps.setDate(i + 1, (Date) v);
            else if (v instanceof Long) ps.setLong(i + 1, (Long) v);
            else if (v instanceof Integer) ps.setInt(i + 1, (Integer) v);
            else                  ps.setString(i + 1, v.toString());
        }
    }
}
