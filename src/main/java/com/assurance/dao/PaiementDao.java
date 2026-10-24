package com.assurance.dao;

import com.assurance.model.ModePaiement;
import com.assurance.model.Paiement;
import com.assurance.model.StatutPaiement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DAO des paiements : historique, paiements en attente, échéances, chiffre d'affaires.
 */
public class PaiementDao extends GenericDao<Paiement, Long> {

    private static final String FETCH =
            "select p from Paiement p " +
            "join fetch p.contrat c " +
            "join fetch c.client ";

    public PaiementDao() {
        super(Paiement.class);
    }

    // ===================== LECTURE =====================

    public List<Paiement> findAllComplet() {
        return inSession(session ->
                session.createQuery(FETCH + "order by p.date desc", Paiement.class).list());
    }

    public List<Paiement> findByClient(Long clientId) {
        return inSession(session ->
                session.createQuery(FETCH + "where c.client.id = :id order by p.date desc", Paiement.class)
                        .setParameter("id", clientId).list());
    }

    public List<Paiement> findByClientAndStatut(Long clientId, StatutPaiement statut) {
        return inSession(session ->
                session.createQuery(FETCH + "where c.client.id = :id and p.statut = :st " +
                                "order by p.date", Paiement.class)
                        .setParameter("id", clientId).setParameter("st", statut).list());
    }

    public List<Paiement> findByContrat(Long contratId) {
        return inSession(session ->
                session.createQuery(FETCH + "where c.id = :id order by p.date desc", Paiement.class)
                        .setParameter("id", contratId).list());
    }

    public List<Paiement> findByStatut(StatutPaiement statut) {
        return inSession(session ->
                session.createQuery(FETCH + "where p.statut = :st order by p.date", Paiement.class)
                        .setParameter("st", statut).list());
    }

    /** Échéances : paiements en attente d'un client dans les N prochains jours (ou en retard). */
    public List<Paiement> findEcheances(Long clientId, int joursAVenir) {
        return inSession(session ->
                session.createQuery(FETCH + "where c.client.id = :id and p.statut = :st " +
                                "and p.date <= :limite order by p.date", Paiement.class)
                        .setParameter("id", clientId)
                        .setParameter("st", StatutPaiement.EN_ATTENTE)
                        .setParameter("limite", LocalDate.now().plusDays(joursAVenir))
                        .list());
    }

    // ===================== ÉCRITURE =====================

    /** L'agent valide un paiement en attente. */
    public void marquerEffectue(Long paiementId) {
        inTransaction(session -> {
            Paiement p = session.get(Paiement.class, paiementId);
            if (p == null) throw new IllegalArgumentException("Paiement introuvable : " + paiementId);
            p.setStatut(StatutPaiement.EFFECTUE);
            p.setDate(LocalDate.now());
        });
    }

    /** Change le statut ; met la date à aujourd'hui si EFFECTUE. */
    public void changerStatut(Long paiementId, StatutPaiement nouveau, ModePaiement mode) {
        inTransaction(session -> {
            Paiement p = session.get(Paiement.class, paiementId);
            if (p == null) throw new IllegalArgumentException("Paiement introuvable : " + paiementId);
            p.setStatut(nouveau);
            if (mode != null) p.setModePaiement(mode);
            if (nouveau == StatutPaiement.EFFECTUE) p.setDate(LocalDate.now());
        });
    }

    public Paiement findByIdComplet(Long id) {
        return inSession(session ->
                session.createQuery(FETCH + "where p.id = :id", Paiement.class)
                        .setParameter("id", id).uniqueResult());
    }

    // ===================== STATISTIQUES =====================

    /** Chiffre d'affaires = somme des paiements effectués. */
    public BigDecimal chiffreAffaires() {
        BigDecimal total = inSession(session ->
                session.createQuery("select sum(p.montant) from Paiement p where p.statut = :st", BigDecimal.class)
                        .setParameter("st", StatutPaiement.EFFECTUE).uniqueResult());
        return total != null ? total : BigDecimal.ZERO;
    }

    /** Évolution des revenus : [année, mois, montant]. */
    public List<Object[]> revenusParMois() {
        return inSession(session ->
                session.createQuery("select year(p.date), month(p.date), sum(p.montant) " +
                                "from Paiement p where p.statut = :st " +
                                "group by year(p.date), month(p.date) " +
                                "order by year(p.date), month(p.date)", Object[].class)
                        .setParameter("st", StatutPaiement.EFFECTUE).list());
    }
}