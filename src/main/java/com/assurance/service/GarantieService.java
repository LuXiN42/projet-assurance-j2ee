package com.assurance.service;

import com.assurance.dao.ContratDao;
import com.assurance.dao.GarantieDao;
import com.assurance.model.Contrat;
import com.assurance.model.Garantie;
import com.assurance.model.StatutContrat;

import java.util.List;

public class GarantieService {

    private final GarantieDao garantieDao;
    private final ContratDao contratDao;

    public GarantieService() { this(new GarantieDao(), new ContratDao()); }
    public GarantieService(GarantieDao g, ContratDao c) { this.garantieDao = g; this.contratDao = c; }

    public List<Garantie> lister() {
        return garantieDao.findAll();
    }

    public Garantie trouver(Long id) {
        Garantie g = id == null ? null : garantieDao.findById(id);
        if (g == null) throw new BusinessException("Garantie introuvable : " + id);
        return g;
    }

    public Garantie creer(String nom, String description) {
        String n = valider(nom, description);
        Garantie existante = garantieDao.findByNom(n);
        if (existante != null) throw new BusinessException("Une garantie « " + n + " » existe déjà.");
        Garantie g = new Garantie();
        g.setNom(n);
        g.setDescription(vide(description) ? null : description.trim());
        garantieDao.save(g);
        return g;
    }

    public Garantie modifier(Long id, String nom, String description) {
        String n = valider(nom, description);
        Garantie g = trouver(id);
        Garantie autre = garantieDao.findByNom(n);
        if (autre != null && !autre.getId().equals(id)) {
            throw new BusinessException("Une autre garantie « " + n + " » existe déjà.");
        }
        g.setNom(n);
        g.setDescription(vide(description) ? null : description.trim());
        garantieDao.update(g);
        return g;
    }

    public void supprimer(Long id) {
        Garantie g = trouver(id);
        if (garantieDao.estUtilisee(id)) {
            throw new BusinessException("Impossible de supprimer « " + g.getNom()
                    + " » : elle est associée à au moins un contrat.");
        }
        garantieDao.delete(g);
    }

    // ---------- association à un contrat ----------

    public List<Garantie> garantiesDuContrat(Long contratId) {
        return garantieDao.findByContrat(contratId);
    }

    public void associer(Long garantieId, Long contratId) {
        Contrat c = contratModifiable(contratId);
        trouver(garantieId);
        for (Garantie g : garantieDao.findByContrat(c.getId())) {
            if (g.getId().equals(garantieId)) {
                throw new BusinessException("Cette garantie est déjà associée au contrat.");
            }
        }
        garantieDao.associerAContrat(garantieId, contratId);
    }

    public void dissocier(Long garantieId, Long contratId) {
        contratModifiable(contratId);
        garantieDao.dissocierDeContrat(garantieId, contratId);
    }

    private Contrat contratModifiable(Long contratId) {
        Contrat c = contratId == null ? null : contratDao.findByIdComplet(contratId);
        if (c == null) throw new BusinessException("Contrat introuvable : " + contratId);
        if (c.getStatut() == StatutContrat.RESILIE || c.getStatut() == StatutContrat.EXPIRE) {
            throw new BusinessException("Un contrat " + c.getStatut() + " ne peut plus être modifié.");
        }
        return c;
    }

    private String valider(String nom, String description) {
        if (vide(nom)) throw new BusinessException("Le nom de la garantie est obligatoire.");
        String n = nom.trim();
        if (n.length() > 150) throw new BusinessException("Le nom ne doit pas dépasser 150 caractères.");
        if (description != null && description.length() > 2000) {
            throw new BusinessException("La description ne doit pas dépasser 2000 caractères.");
        }
        return n;
    }

    private static boolean vide(String s) { return s == null || s.isBlank(); }
}