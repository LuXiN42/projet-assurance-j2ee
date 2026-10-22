package com.assurance.dao;

import com.assurance.model.Contrat;
import com.assurance.model.StatutContrat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DAO des contrats : recherche, changements de statut, statistiques.
 */
public class ContratDao extends GenericDao<Contrat, Long> {

    /** Charge le client, le type et les garanties en une seule requête. */
    private static final String FETCH =
            "select distinct c from Contrat c " +
            "join fetch c.client " +
            "join fetch c.typeAssurance " +
            "left join fetch c.garanties ";

    public ContratDao() {
        super(Contrat.class);
    }

    // ===================== LECTURE =====================

    public Contrat findByIdComplet(Long id) {
        return inSession(session ->
                session.createQuery(FETCH + "where c.id = :id", Contrat.class)
                        .setParameter("id", id).uniqueResult());
    }

    public Contrat findByNumero(String numero) {
        return inSession(session ->
                session.createQuery(FETCH + "where c.numero = :numero", Contrat.class)
                        .setParameter("numero", numero).uniqueResult());
    }

    public List<Contrat> findAllComplet() {
        return inSession(session ->
                session.createQuery(FETCH + "order by c.dateDebut desc", Contrat.class).list());
    }

    public List<Contrat> findByClient(Long clientId) {
        return inSession(session ->
                session.createQuery(FETCH + "where c.client.id = :id order by c.dateDebut desc", Contrat.class)
                        .setParameter("id", clientId).list());
    }

    public List<Contrat> findByClientAndStatut(Long clientId, StatutContrat statut) {
        return inSession(session ->
                session.createQuery(FETCH + "where c.client.id = :id and c.statut = :statut", Contrat.class)
                        .setParameter("id", clientId).setParameter("statut", statut).list());
    }

    public List<Contrat> findByStatut(StatutContrat statut) {
        return inSession(session ->
                session.createQuery(FETCH + "where c.statut = :statut", Contrat.class)
                        .setParameter("statut", statut).list());
    }

    /** Recherche multicritère : chaque paramètre est optionnel. */
    public List<Contrat> rechercher(String numero, String nomClient, Long typeId, StatutContrat statut) {
        boolean avecNumero = numero != null && !numero.isBlank();
        boolean avecNom = nomClient != null && !nomClient.isBlank();

        StringBuilder hql = new StringBuilder(FETCH + "where 1=1 ");
        if (avecNumero)     hql.append("and c.numero like :numero ");
        if (avecNom)        hql.append("and lower(c.client.nom) like :nom ");
        if (typeId != null) hql.append("and c.typeAssurance.id = :typeId ");
        if (statut != null) hql.append("and c.statut = :statut ");
        hql.append("order by c.dateDebut desc");

        return inSession(session -> {
            var query = session.createQuery(hql.toString(), Contrat.class);
            if (avecNumero)     query.setParameter("numero", "%" + numero.trim() + "%");
            if (avecNom)        query.setParameter("nom", "%" + nomClient.toLowerCase().trim() + "%");
            if (typeId != null) query.setParameter("typeId", typeId);
            if (statut != null) query.setParameter("statut", statut);
            return query.list();
        });
    }

    // ===================== CHANGEMENTS DE STATUT =====================

    public void changerStatut(Long contratId, StatutContrat nouveauStatut) {
        inTransaction(session -> {
            Contrat c = session.get(Contrat.class, contratId);
            if (c == null) throw new IllegalArgumentException("Contrat introuvable : " + contratId);
            c.setStatut(nouveauStatut);
        });
    }

    public void suspendre(Long contratId) {
        changerStatut(contratId, StatutContrat.SUSPENDU);
    }

    public void resilier(Long contratId) {
        changerStatut(contratId, StatutContrat.RESILIE);
    }

    /** Prolonge le contrat de N mois à partir de sa date de fin. */
    public void renouveler(Long contratId, int dureeMois) {
        inTransaction(session -> {
            Contrat c = session.get(Contrat.class, contratId);
            if (c == null) throw new IllegalArgumentException("Contrat introuvable : " + contratId);
            if (c.getStatut() == StatutContrat.RESILIE) {
                throw new IllegalStateException("Un contrat résilié ne peut pas être renouvelé");
            }
            LocalDate base = c.getDateFin().isBefore(LocalDate.now()) ? LocalDate.now() : c.getDateFin();
            c.setDateFin(base.plusMonths(dureeMois));
            c.setStatut(StatutContrat.ACTIF);
        });
    }

    /** Passe en EXPIRE les contrats actifs dont la date de fin est dépassée. */
    public int mettreAJourContratsExpires() {
        return inTransactionWithResult(session ->
                session.createMutationQuery("update Contrat c set c.statut = :expire " +
                                "where c.dateFin < :today and c.statut = :actif")
                        .setParameter("expire", StatutContrat.EXPIRE)
                        .setParameter("actif", StatutContrat.ACTIF)
                        .setParameter("today", LocalDate.now())
                        .executeUpdate());
    }

    /** Génère un numéro. */
    public String genererNumero() {
        return String.format("CTR-%d-%05d", LocalDate.now().getYear(), count() + 1);
    }

    // ===================== STATISTIQUES =====================

    public long countByStatut(StatutContrat statut) {
        return inSession(session ->
                session.createQuery("select count(c) from Contrat c where c.statut = :s", Long.class)
                        .setParameter("s", statut).uniqueResult());
    }

    /** Répartition. */
    public List<Object[]> countByTypeAssurance() {
        return inSession(session ->
                session.createQuery("select c.typeAssurance.nom, count(c) from Contrat c " +
                                "group by c.typeAssurance.nom", Object[].class).list());
    }

    /** Évolution. */
    public List<Object[]> evolutionParMois() {
        return inSession(session ->
                session.createQuery("select year(c.dateDebut), month(c.dateDebut), count(c) " +
                                "from Contrat c group by year(c.dateDebut), month(c.dateDebut) " +
                                "order by year(c.dateDebut), month(c.dateDebut)", Object[].class).list());
    }

    public BigDecimal totalPrimesActives() {
        BigDecimal total = inSession(session ->
                session.createQuery("select sum(c.prime) from Contrat c where c.statut = :s", BigDecimal.class)
                        .setParameter("s", StatutContrat.ACTIF).uniqueResult());
        return total != null ? total : BigDecimal.ZERO;
    }
}