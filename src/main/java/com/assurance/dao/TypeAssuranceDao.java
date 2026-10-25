package com.assurance.dao;

import java.util.List;

import com.assurance.model.TypeAssurance;

/**
 * DAO des types d'assurance.
 */
public class TypeAssuranceDao extends GenericDao<TypeAssurance, Long> {

    public TypeAssuranceDao() {
        super(TypeAssurance.class);
    }

    public TypeAssurance findByNom(String nom) {
        return inSession(session ->
                session.createQuery("from TypeAssurance t where t.nom = :nom", TypeAssurance.class)
                        .setParameter("nom", nom).uniqueResult());
    }

    public boolean existsByNom(String nom) {
        return findByNom(nom) != null;
    }

    /** Charge le type avec ses garanties. */
    public TypeAssurance findByIdWithGaranties(Long id) {
        return inSession(session ->
                session.createQuery("select distinct t from TypeAssurance t " +
                                "left join fetch t.garantiesDisponibles where t.id = :id", TypeAssurance.class)
                        .setParameter("id", id).uniqueResult());
    }

    public List<TypeAssurance> findAllOrderByNom() {
        return inSession(session ->
                session.createQuery("from TypeAssurance t order by t.nom", TypeAssurance.class).list());
    }

        /** Un type utilisé par au moins un contrat ne doit pas être supprimé. */
    public boolean estUtilise(Long typeId) {
        Long nb = inSession(session ->
                session.createQuery("select count(c) from Contrat c where c.typeAssurance.id = :id", Long.class)
                        .setParameter("id", typeId).uniqueResult());
        return nb != null && nb > 0;
    }
}