package com.assurance.util;

import com.assurance.dao.UtilisateurDao;
import com.assurance.model.Role;
import com.assurance.model.Utilisateur;

public class CreerAdmin {
    public static void main(String[] args) {
        UtilisateurDao dao = new UtilisateurDao();
        String email = "admin@assurance.fr";
        if (dao.existsByEmail(email)) {
            System.out.println("Admin déjà existant");
        } else {
            Utilisateur a = new Utilisateur();
            a.setEmail(email);
            a.setNom("Admin");
            a.setPrenom("Super");
            a.setRole(Role.ADMIN);
            a.setPasswordHash(PasswordUtil.hacher("Admin1234"));
            dao.save(a);
            System.out.println("Admin créé : " + email + " / Admin1234");
        }
        System.exit(0);
    }
}