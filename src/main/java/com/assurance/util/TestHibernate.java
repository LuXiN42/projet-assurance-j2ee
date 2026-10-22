package com.assurance.util;

public class TestHibernate {
    public static void main(String[] args) {
        HibernateUtil.getSessionFactory();
        System.out.println("Connexion OK, tables créées");
        System.exit(0);
    }
}