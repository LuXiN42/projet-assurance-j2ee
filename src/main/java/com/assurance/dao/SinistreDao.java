package com.assurance.dao;

import com.assurance.model.Commentaire;
import com.assurance.model.Sinistre;
import com.assurance.model.StatutSinistre;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public Sinistre findByIdAvecCommentaires(Long id) {
        return inSession(session -> {
            Sinistre s = session.createQuery(
                    "select distinct s from Sinistre s " +
                    "left join fetch s.commentaires c " +
                    "left join fetch c.auteur " +
                    "join fetch s.contrat ct " +
                    "left join fetch ct.typeAssurance " +
                    "join fetch ct.client " +
                    "where s.id = :id", Sinistre.class)
                    .setParameter("id", id)
                    .uniqueResult();
            if (s != null) {
                // Charge les pièces dans la même session (2e requête)
                session.createQuery(
                        "select distinct s from Sinistre s " +
                        "left join fetch s.pieces " +
                        "where s.id = :id", Sinistre.class)
                        .setParameter("id", id)
                        .uniqueResult();
            }
            return s;
        });
    }


    public Sinistre findByNumero(String numero) {
        return inSession(session ->
                session.createQuery(FETCH + "where s.numeroSinistre = :n", Sinistre.class)
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

    public Sinistre findByIdAvecPieces(Long id) {
        return inSession(s -> s.createQuery(
                "select distinct si from Sinistre si " +
                "left join fetch si.pieces join fetch si.contrat c left join fetch c.typeAssurance " +
                "where si.id = :id", Sinistre.class)
            .setParameter("id", id)
            .uniqueResult());
    }

    // ===================== TRAITEMENT PAR L'AGENT =====================

    public void changerStatut(Long sinistreId, StatutSinistre statut) {
        inTransaction(session -> {
            Sinistre s = session.get(Sinistre.class, sinistreId);
            if (s == null) throw new IllegalArgumentException("Sinistre introuvable : " + sinistreId);
            s.setStatut(statut);
        });
    }

    public void ajouterCommentaire(Long sinistreId, Commentaire commentaire) {
            inTransaction(session -> {
                Sinistre s = session.get(Sinistre.class, sinistreId);
                if (s == null) throw new IllegalArgumentException("Sinistre introuvable : " + sinistreId);
                if (commentaire.getAuteur() == null) {
                    throw new IllegalArgumentException("L'auteur du commentaire est obligatoire");
                }
                commentaire.setDateCreation(
                        commentaire.getDateCreation() != null ? commentaire.getDateCreation()
                                                            : LocalDateTime.now());
                s.ajouterCommentaire(commentaire);
                // Pas besoin de session.save : cascade.ALL + lien bidirectionnel suffisent.
            });
        }


    public void traiterSinistre(Long sinistreId,
                                StatutSinistre nouveauStatut,
                                BigDecimal montantIndemnisation) {
        inTransaction(session -> {
            Sinistre s = session.get(Sinistre.class, sinistreId);
            if (s == null) throw new IllegalArgumentException("Sinistre introuvable : " + sinistreId);

            // Seul un sinistre ACCEPTE (ou déjà INDEMNISE, pour corriger le montant) peut être indemnisé
            if (nouveauStatut == StatutSinistre.INDEMNISE
                    && s.getStatut() != StatutSinistre.ACCEPTE
                    && s.getStatut() != StatutSinistre.INDEMNISE) {
                throw new IllegalStateException("Seul un sinistre ACCEPTE peut être indemnisé");
            }

            if (montantIndemnisation != null && montantIndemnisation.signum() < 0) {
                throw new IllegalArgumentException("Le montant d'indemnisation ne peut pas être négatif");
            }

            if (montantIndemnisation != null
                    && nouveauStatut != StatutSinistre.ACCEPTE
                    && nouveauStatut != StatutSinistre.INDEMNISE) {
                throw new IllegalStateException(
                        "Le montant d'indemnisation n'est autorisé que pour les statuts ACCEPTE ou INDEMNISE");
            }

            if (nouveauStatut == StatutSinistre.INDEMNISE && montantIndemnisation == null) {
                throw new IllegalStateException(
                        "Un montant d'indemnisation est obligatoire pour passer le sinistre à INDEMNISE");
            }

            s.setStatut(nouveauStatut);
            s.setMontantIndemnisation(montantIndemnisation);
        });
    }

    /** Enregistre l'indemnisation : seul un sinistre ACCEPTE peut être indemnisé. */
    public void indemniser(Long sinistreId, BigDecimal montant) {
        traiterSinistre(sinistreId, StatutSinistre.INDEMNISE, montant);
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