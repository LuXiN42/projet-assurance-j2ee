package com.assurance.dao;

import com.assurance.model.Commentaire;

import java.util.List;

/** DAO des commentaires. */
public class CommentaireDao extends GenericDao<Commentaire, Long> {

    public CommentaireDao() {
        super(Commentaire.class);
    }

    /** Commentaires d'un sinistre, du plus ancien au plus récent. */
    public List<Commentaire> findBySinistre(Long sinistreId) {
        return inSession(session ->
                session.createQuery(
                        "select c from Commentaire c " +
                        "where c.sinistre.id = :id " +
                        "order by c.dateCreation asc", Commentaire.class)
                        .setParameter("id", sinistreId)
                        .list());
    }

    /** Commentaires écrits par un agent. */
    public List<Commentaire> findByAuteur(Long auteurId) {
        return inSession(session ->
                session.createQuery(
                        "select c from Commentaire c " +
                        "where c.auteur.id = :id " +
                        "order by c.dateCreation desc", Commentaire.class)
                        .setParameter("id", auteurId)
                        .list());
    }

    /** Commentaire avec son auteur déjà chargé (sinon LazyInitializationException). */
    public Commentaire findByIdWithAuteur(Long id) {
        return inSession(session ->
                session.createQuery(
                        "select c from Commentaire c " +
                        "join fetch c.auteur " +
                        "where c.id = :id", Commentaire.class)
                        .setParameter("id", id)
                        .uniqueResult());
    }
}
