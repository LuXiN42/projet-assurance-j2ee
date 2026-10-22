package com.assurance.dao;

import com.assurance.model.Sinistre;
import com.assurance.model.StatutSinistre;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DAO des sinistres : déclaration, suivi, traitement par l'agent.
 */
public class SinistreDao extends GenericDao<Sinistre, Long> {

    private static final String FETCH =
            "select s from Sinistre s " +
            "join fetch s.contrat c " +
            "join fetch c.client ";

    public SinistreDao() {
        super(Sinistre.class);
    }

    // ===================== LECTURE =====================

    public Sinistre findByIdComplet(Long id) {
        return inSession(session ->
                session.createQuery(FETCH + "where s.id = :id", Sinistre.class)
                        .setParameter("id", id).uniqueResult());
    }

    public Sinistre findByNumero(String numero) {
        return inSession(session ->
                session.createQuery(FETCH + "where s.numero = :n", Sinistre.class)
                        .setParameter("n", numero).uniqueResult());
    }

    public List<Sinistre> findAllComplet() {
        return inSession(session ->
                session.createQuery(FETCH + "order by s.date desc", Sinistre.class).list());
    }

    public List<Sinistre> findByClient(Long clientId) {
        return inSession(session ->
                session.createQuery(FETCH + "where c.client.id = :id order by s.date desc", Sinistre.class)
                        .setParameter("id", clientId).list());
    }

    /** Dernier sinistre d'un client. */
    public Sinistre findDernierByClient(Long clientId) {
        return inSession(session ->
                session.createQuery(FETCH + "where c.client.id = :id " +
                                "order by s.date desc, s.id desc", Sinistre.class)
                        .setParameter("id", clientId).setMaxResults(1).uniqueResult());
    }

    public List<Sinistre> findByContrat(Long contratId) {
        return inSession(session ->
                session.createQuery(FETCH + "where c.id = :id order by s.date desc", Sinistre.class)
                        .setParameter("id", contratId).list());
    }

    public List<Sinistre> findByStatut(StatutSinistre statut) {
        return inSession(session ->
                session.createQuery(FETCH + "where s.statut = :st order by s.date", Sinistre.class)
                        .setParameter("st", statut).list());
    }

    // ===================== TRAITEMENT PAR L'AGENT =====================

    public void changerStatut(Long sinistreId, StatutSinistre statut, String commentaire) {
        inTransaction(session -> {
            Sinistre s = session.get(Sinistre.class, sinistreId);
            if (s == null) throw new IllegalArgumentException("Sinistre introuvable : " + sinistreId);
            s.setStatut(statut);
            if (commentaire != null && !commentaire.isBlank()) {
                s.setCommentaires(commentaire);
            }
        });
    }

    /** Enregistre l'indemnisation : seul un sinistre ACCEPTE peut être indemnisé. */
    public void indemniser(Long sinistreId, BigDecimal montant) {
        inTransaction(session -> {
            Sinistre s = session.get(Sinistre.class, sinistreId);
            if (s == null) throw new IllegalArgumentException("Sinistre introuvable : " + sinistreId);
            if (s.getStatut() != StatutSinistre.ACCEPTE) {
                throw new IllegalStateException("Seul un sinistre ACCEPTE peut être indemnisé");
            }
            if (montant == null || montant.signum() <= 0) {
                throw new IllegalArgumentException("Montant d'indemnisation invalide");
            }
            s.setMontantIndemnisation(montant);
            s.setStatut(StatutSinistre.INDEMNISE);
        });
    }

    /** Génère un numéro, ex. SIN-2026-00007. */
    public String genererNumero() {
        return String.format("SIN-%d-%05d", LocalDate.now().getYear(), count() + 1);
    }

    // ===================== STATISTIQUES =====================

    public long countByStatut(StatutSinistre statut) {
        return inSession(session ->
                session.createQuery("select count(s) from Sinistre s where s.statut = :st", Long.class)
                        .setParameter("st", statut).uniqueResult());
    }

    public BigDecimal totalIndemnisations() {
        BigDecimal total = inSession(session ->
                session.createQuery("select sum(s.montantIndemnisation) from Sinistre s " +
                                "where s.statut = :st", BigDecimal.class)
                        .setParameter("st", StatutSinistre.INDEMNISE).uniqueResult());
        return total != null ? total : BigDecimal.ZERO;
    }
}