package com.assurance.util;

import com.assurance.dao.PaiementDao;
import com.assurance.dao.SinistreDao;

public class TestHibernate {
    public static void main(String[] args) {
        HibernateUtil.getSessionFactory();
        System.out.println("Connexion OK, tables créées");

        // Test des requêtes HQL des DAO
        System.out.println("Sinistres : " + new SinistreDao().findAllComplet().size());
        System.out.println("Paiements : " + new PaiementDao().findAllComplet().size());

        System.exit(0);
    }
}