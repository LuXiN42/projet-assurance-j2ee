package com.assurance.dao;

import com.assurance.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * DAO générique : contient le CRUD commun à toutes les entités.
 * Chaque DAO concret hérite de cette classe et n'ajoute que ses requêtes spécifiques.
 *
 * @param <T>  type de l'entité 
 * @param <ID> type de la clé primaire
 */
public abstract class GenericDao<T, ID> {

    private final Class<T> type;

    protected GenericDao(Class<T> type) {
        this.type = type;
    }

    // ===================== CREATE =====================

    public void save(T entity) {
        inTransaction(session -> session.persist(entity));
    }

    // ===================== READ =====================

    public T findById(ID id) {
        return inSession(session -> session.get(type, id));
    }

    public List<T> findAll() {
        return inSession(session ->
                session.createQuery("from " + type.getSimpleName(), type).list());
    }

    public long count() {
        return inSession(session ->
                session.createQuery("select count(e) from " + type.getSimpleName() + " e", Long.class).uniqueResult());
    }

    // ===================== UPDATE =====================

    public T update(T entity) {
        return inTransactionWithResult(session -> session.merge(entity));
    }

    // ===================== DELETE =====================

    public void delete(T entity) {
        inTransaction(session ->
                session.remove(session.contains(entity) ? entity : session.merge(entity)));
    }

    public void deleteById(ID id) {
        inTransaction(session -> {
            T entity = session.get(type, id);
            if (entity != null) {
                session.remove(entity);
            }
        });
    }

    // ===================== OUTILS (réutilisables par les DAO enfants) =====================

    /** Lecture simple, sans transaction. */
    protected <R> R inSession(Function<Session, R> action) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return action.apply(session);
        }
    }

    /** Écriture dans une transaction, sans valeur de retour. */
    protected void inTransaction(Consumer<Session> action) {
        inTransactionWithResult(session -> {
            action.accept(session);
            return null;
        });
    }

    /** Écriture dans une transaction, avec valeur de retour. Rollback en cas d'erreur. */
    protected <R> R inTransactionWithResult(Function<Session, R> action) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            R result = action.apply(session);
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();  
            }
            throw e;
        }
    }
}