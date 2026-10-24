package com.assurance.dao;

import com.assurance.model.PieceJustificative;

public class PieceJustificativeDao extends GenericDao<PieceJustificative, Long> {

    public PieceJustificativeDao() { super(PieceJustificative.class); }

    /** Charge la pièce avec sinistre, contrat et client (pour vérifier le propriétaire). */
    public PieceJustificative findByIdAvecProprietaire(Long id) {
        return inSession(s -> s.createQuery(
                "select p from PieceJustificative p " +
                "join fetch p.sinistre si join fetch si.contrat c join fetch c.client " +
                "where p.id = :id", PieceJustificative.class)
                .setParameter("id", id)
                .uniqueResult());
    }
}