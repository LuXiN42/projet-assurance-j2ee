package com.assurance.dao;

import com.assurance.model.Contrat;
import com.assurance.model.Garantie;
import com.assurance.model.TypeAssurance;

import java.util.List;

/**
 * DAO des garanties.
 */
public class GarantieDao extends GenericDao<Garantie, Long> {

    public GarantieDao() {
        super(Garantie.class);
    }

    public Garantie findByNom(String nom) {
        return inSession(session ->
                session.createQuery("from Garantie g where g.nom = :nom", Garantie.class)
                        .setParameter("nom", nom).uniqueResult());
    }

    /** Garanties d'un contrat. */
    public List<Garantie> findByContrat(Long contratId) {
        return inSession(session ->
                session.createQuery("select g from Contrat c join c.garanties g " +
                                "where c.id = :id order by g.nom", Garantie.class)
                        .setParameter("id", contratId).list());
    }

    /** Garanties proposées par un type d'assurance. */
    public List<Garantie> findByTypeAssurance(Long typeId) {
        return inSession(session ->
                session.createQuery("select g from TypeAssurance t join t.garantiesDisponibles g " +
                                "where t.id = :id order by g.nom", Garantie.class)
                        .setParameter("id", typeId).list());
    }

    /** Associe une garantie à un contrat. */
    public void associerAContrat(Long garantieId, Long contratId) {
        inTransaction(session -> {
            Contrat contrat = session.get(Contrat.class, contratId);
            Garantie garantie = session.get(Garantie.class, garantieId);
            if (contrat == null || garantie == null) {
                throw new IllegalArgumentException("Contrat ou garantie introuvable");
            }
            if (!contrat.getGaranties().contains(garantie)) {
                contrat.getGaranties().add(garantie);
            }
        });
    }

    /** Retire une garantie d'un contrat. */
    public void dissocierDeContrat(Long garantieId, Long contratId) {
        inTransaction(session -> {
            Contrat contrat = session.get(Contrat.class, contratId);
            if (contrat != null) {
                contrat.getGaranties().removeIf(g -> g.getId().equals(garantieId));
            }
        });
    }

    /** Une garantie utilisée par un contrat ne doit pas être supprimée. */
    public boolean estUtilisee(Long garantieId) {
        Long nb = inSession(session ->
                session.createQuery("select count(c) from Contrat c join c.garanties g " +
                                "where g.id = :id", Long.class)
                        .setParameter("id", garantieId).uniqueResult());
        return nb != null && nb > 0;
    }

    /** Nombre de types d'assurance qui proposent cette garantie. */
    public long compterTypesAssurance(Long garantieId) {
        Long nb = inSession(session ->
                session.createQuery("select count(t) from TypeAssurance t " +
                                "join t.garantiesDisponibles g where g.id = :id", Long.class)
                        .setParameter("id", garantieId).uniqueResult());
        return nb == null ? 0 : nb;
    }

    /** Retire la garantie de tous les types d'assurance qui la proposent. */
    public void detacherDesTypes(Long garantieId) {
        inTransaction(session -> {
            Garantie g = session.get(Garantie.class, garantieId);
            if (g == null) return;
            List<TypeAssurance> types = session.createQuery(
                    "select t from TypeAssurance t join t.garantiesDisponibles x where x.id = :id",
                    TypeAssurance.class)
                    .setParameter("id", garantieId).list();
            for (TypeAssurance t : types) {
                t.getGarantiesDisponibles().removeIf(x -> x.getId().equals(garantieId));
            }
        });
    }
}