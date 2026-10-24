package com.assurance.dao;

import com.assurance.model.Role;
import com.assurance.model.Utilisateur;

import java.util.List;

/**
 * DAO des utilisateurs (clients, agents, administrateurs).
 * Le CRUD de base (save, findById, findAll, update, delete, count) est hérité de GenericDao.
 */
public class UtilisateurDao extends GenericDao<Utilisateur, Long> {

    public UtilisateurDao() {
        super(Utilisateur.class);
    }

    // ===================== AUTHENTIFICATION =====================

    /** Retrouve un utilisateur par son email (utilisé au login). Renvoie null si inexistant. */
    public Utilisateur findByEmail(String email) {
        return inSession(session ->
                session.createQuery("from Utilisateur u where u.email = :email", Utilisateur.class).setParameter("email", email).uniqueResult());
    }

    /** Vérifie si un email est déjà utilisé */
    public boolean existsByEmail(String email) {
        Long nb = inSession(session ->
                session.createQuery("select count(u) from Utilisateur u where u.email = :email", Long.class).setParameter("email", email).uniqueResult());
        return nb != null && nb > 0;
    }

    // ===================== GESTION PAR RÔLE =====================

    /** Liste les utilisateurs d'un rôle donné */
    public List<Utilisateur> findByRole(Role role) {
        return inSession(s -> s.createQuery(
                "from Utilisateur u where u.role = :r order by u.nom, u.prenom", Utilisateur.class)
            .setParameter("r", role).list());
    }

    /** Nombre d'utilisateurs d'un rôle */
    public long countByRole(Role role) {
        return inSession(session ->
                session.createQuery("select count(u) from Utilisateur u where u.role = :role", Long.class).setParameter("role", role).uniqueResult());
    }

    /** Change le rôle d'un utilisateur. */
    public void updateRole(Long id, Role nouveauRole) {
        inTransaction(session -> {
            Utilisateur u = session.get(Utilisateur.class, id);
            if (u != null) {
                u.setRole(nouveauRole);   // Hibernate enregistre la modification au commit
            }
        });
    }

    // ===================== RECHERCHE =====================

    /** Recherche par nom, prénom ou email. */
    public List<Utilisateur> search(String motCle) {
        String pattern = "%" + motCle.toLowerCase().trim() + "%";
        return inSession(session ->
                session.createQuery(
                        "from Utilisateur u " +
                        "where lower(u.nom) like :p or lower(u.prenom) like :p or lower(u.email) like :p " +
                        "order by u.nom", Utilisateur.class).setParameter("p", pattern).list());
    }

    /** Nombre de clients inscrits depuis une date (statistique "nouveaux clients"). */
    public long countNouveauxClients(java.time.LocalDate depuis) {
        return inSession(session ->
                session.createQuery(
                        "select count(u) from Utilisateur u " +
                        "where u.role = :role and u.dateInscription >= :depuis", Long.class).setParameter("role", Role.CLIENT).setParameter("depuis", depuis).uniqueResult());
        }
}