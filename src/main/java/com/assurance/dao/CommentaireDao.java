package com.assurance.dao;

import com.assurance.model.Commentaire;

import java.util.List;

/**
 * DAO des commentaires. Hérite du CRUD générique de {@link GenericDao}.
 * Le critère "récents d'abord" ou "tous" se fait via les méthodes spécifiques
 * ci-dessous ; le tri ascendant côté entité est garanti par le {@code @OrderBy}
 * sur {@code Sinistre.commentaires}.
 */
public class CommentaireDao extends GenericDao<Commentaire, Long> {

    public CommentaireDao() {
        super(Commentaire.class);
    }

    /**
     * Tous les commentaires d'un sinistre, triés du plus ancien au plus récent.
     * Renvoie une liste (et non un Set) pour conserver l'ordre chronologique.
     */
    public List<Commentaire> findBySinistre(Long sinistreId) {
        return inSession(session ->
                session.createQuery(
                        "select c from Commentaire c " +
                        "where c.sinistre.id = :id " +
                        "order by c.dateCreation asc", Commentaire.class)
                        .setParameter("id", sinistreId)
                        .list());
    }

    /**
     * Commentaires écrits par un agent (auteur), pour audit / statistiques.
     */
    public List<Commentaire> findByAuteur(Long auteurId) {
        return inSession(session ->
                session.createQuery(
                        "select c from Commentaire c " +
                        "where c.auteur.id = :id " +
                        "order by c.dateCreation desc", Commentaire.class)
                        .setParameter("id", auteurId)
                        .list());
    }

    /**
     * Charge un commentaire avec son auteur (évite LazyInitializationException
     * si l'on veut afficher le nom de l'agent dans une vue).
     */
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
