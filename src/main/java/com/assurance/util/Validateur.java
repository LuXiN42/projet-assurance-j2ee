package com.assurance.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/** Validation commune des champs de formulaire : accumule les messages d'erreur. */
public class Validateur {

    private static final Pattern EMAIL =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern TELEPHONE =
            Pattern.compile("^\\+?[0-9 ]{8,15}$");

    private final List<String> erreurs = new ArrayList<>();

    public List<String> getErreurs() { return erreurs; }
    public boolean estValide()       { return erreurs.isEmpty(); }
    public void ajouter(String msg)  { erreurs.add(msg); }

    /* ---------- chaînes ---------- */

    public String obligatoire(String valeur, String champ) {
        if (FormUtil.isBlank(valeur)) { erreurs.add(champ + " est obligatoire."); return null; }
        return valeur.trim();
    }

    public String texte(String valeur, String champ, int min, int max, boolean requis) {
        if (FormUtil.isBlank(valeur)) {
            if (requis) erreurs.add(champ + " est obligatoire.");
            return null;
        }
        String v = valeur.trim();
        if (v.length() < min || v.length() > max)
            erreurs.add(champ + " doit contenir entre " + min + " et " + max + " caractères.");
        return v;
    }

    public String email(String valeur, String champ) {
        String v = obligatoire(valeur, champ);
        if (v != null && (v.length() > 150 || !EMAIL.matcher(v).matches()))
            erreurs.add(champ + " n'est pas une adresse email valide.");
        return v == null ? null : v.toLowerCase();
    }

    public String telephone(String valeur, String champ, boolean requis) {
        if (FormUtil.isBlank(valeur)) {
            if (requis) erreurs.add(champ + " est obligatoire.");
            return null;
        }
        String v = valeur.trim();
        if (!TELEPHONE.matcher(v).matches())
            erreurs.add(champ + " n'est pas un numéro valide.");
        return v;
    }

    public String motDePasse(String mdp, String confirmation) {
        if (FormUtil.isBlank(mdp)) { erreurs.add("Le mot de passe est obligatoire."); return null; }
        if (mdp.length() < 8 || !mdp.matches(".*[A-Za-z].*") || !mdp.matches(".*[0-9].*"))
            erreurs.add("Le mot de passe doit faire au moins 8 caractères avec lettres et chiffres.");
        if (confirmation != null && !mdp.equals(confirmation))
            erreurs.add("Les mots de passe ne correspondent pas.");
        return mdp;
    }

    /** Mot de passe facultatif (modification) : contrôlé seulement s'il est saisi. */
    public String motDePasseOptionnel(String mdp, String confirmation) {
        if (FormUtil.isBlank(mdp)) return null;   // vide = on garde l'ancien
        return motDePasse(mdp, confirmation);
    }

    /* ---------- nombres ---------- */

    public Long id(String valeur, String champ) {
        if (FormUtil.isBlank(valeur)) { erreurs.add(champ + " est obligatoire."); return null; }
        Long v = FormUtil.parseLong(valeur);
        if (v == null || v <= 0) { erreurs.add(champ + " est invalide."); return null; }
        return v;
    }

    public BigDecimal montant(String valeur, String champ, boolean requis, boolean strictementPositif) {
        if (FormUtil.isBlank(valeur)) {
            if (requis) erreurs.add(champ + " est obligatoire.");
            return null;
        }
        BigDecimal v = FormUtil.parseBigDecimal(valeur);
        if (v == null) { erreurs.add(champ + " doit être un nombre."); return null; }
        int signe = v.signum();
        if (strictementPositif ? signe <= 0 : signe < 0)
            erreurs.add(champ + (strictementPositif ? " doit être supérieur à 0." : " ne peut pas être négatif."));
        if (v.scale() > 2)
            erreurs.add(champ + " ne peut pas avoir plus de 2 décimales.");
        return v;
    }

    /* ---------- dates ---------- */

    public LocalDate date(String valeur, String champ, boolean requis) {
        if (FormUtil.isBlank(valeur)) {
            if (requis) erreurs.add(champ + " est obligatoire.");
            return null;
        }
        LocalDate d = FormUtil.parseDate(valeur);
        if (d == null) erreurs.add(champ + " n'est pas une date valide (AAAA-MM-JJ).");
        return d;
    }

    public void pasDansLeFutur(LocalDate d, String champ) {
        if (d != null && d.isAfter(LocalDate.now()))
            erreurs.add(champ + " ne peut pas être dans le futur.");
    }

    public void periode(LocalDate debut, LocalDate fin) {
        if (debut != null && fin != null && !fin.isAfter(debut))
            erreurs.add("La date de fin doit être postérieure à la date de début.");
    }

    /* ---------- enums ---------- */

    public <E extends Enum<E>> E choix(Class<E> type, String valeur, String champ) {
        if (FormUtil.isBlank(valeur)) { erreurs.add(champ + " est obligatoire."); return null; }
        E e = FormUtil.parseEnum(type, valeur);
        if (e == null) erreurs.add(champ + " a une valeur inconnue.");
        return e;
    }

    /** Entier borné (ex : durée de renouvellement en mois). */
    public Integer entier(String valeur, String champ, int min, int max, boolean requis) {
        if (FormUtil.isBlank(valeur)) {
            if (requis) erreurs.add(champ + " est obligatoire.");
            return null;
        }
        Integer v = FormUtil.parseInt(valeur);
        if (v == null) { erreurs.add(champ + " doit être un nombre entier."); return null; }
        if (v < min || v > max)
            erreurs.add(champ + " doit être compris entre " + min + " et " + max + ".");
        return v;
    }

    /** Valeur obligatoire parmi une liste fermée (ex : paramètre "action"). */
    public String parmi(String valeur, String champ, String... autorisees) {
        String v = obligatoire(valeur, champ);
        if (v != null && !Arrays.asList(autorisees).contains(v)) {
            erreurs.add(champ + " a une valeur inconnue : " + v);
            return null;
        }
        return v;
    }

    /** Toutes les erreurs sur une ligne (pour les redirections ?err=...). */
    public String messages() { return String.join(" ", erreurs); }

    /** Enum optionnel : null si vide, erreur si la valeur saisie est inconnue. */
    public <E extends Enum<E>> E choix(Class<E> type, String valeur, String champ, boolean requis) {
        if (FormUtil.isBlank(valeur)) {
            if (requis) erreurs.add(champ + " est obligatoire.");
            return null;
        }
        E e = FormUtil.parseEnum(type, valeur);
        if (e == null) erreurs.add(champ + " a une valeur inconnue.");
        return e;
    }
}