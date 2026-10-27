package com.assurance.service;

import com.assurance.dao.ContratDao;
import com.assurance.dao.PaiementDao;
import com.assurance.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PaiementService {

    private final PaiementDao paiementDao;
    private final ContratDao contratDao;

    public PaiementService() { this(new PaiementDao(), new ContratDao()); }

    public PaiementService(PaiementDao paiementDao, ContratDao contratDao) {
        this.paiementDao = paiementDao;
        this.contratDao = contratDao;
    }

    // ---------- lecture ----------

    public List<Paiement> tous()                         { return paiementDao.findAllComplet(); }
    public List<Paiement> parStatut(StatutPaiement s)    { return paiementDao.findByStatut(s); }
    public List<Paiement> parContrat(Long contratId)     { return paiementDao.findByContrat(contratId); }
    public List<Paiement> historiqueClient(Long cid)     { return paiementDao.findByClient(cid); }
    public List<Paiement> effectuesClient(Long cid)      { return paiementDao.findByClientAndStatut(cid, StatutPaiement.EFFECTUE); }
    public List<Paiement> enAttenteClient(Long cid)      { return paiementDao.findByClientAndStatut(cid, StatutPaiement.EN_ATTENTE); }
    public List<Paiement> echeancesClient(Long cid, int jours) { return paiementDao.findEcheances(cid, jours); }

    public Paiement detail(Long id) {
        Paiement p = paiementDao.findByIdComplet(id);
        if (p == null) throw new BusinessException("Paiement introuvable : " + id);
        return p;
    }

    // ---------- enregistrement ----------

    /**
     * Enregistre un paiement. Sans date on prend aujourd'hui, sans statut on met EFFECTUE.
     * Un paiement EN_ATTENTE sert pour une échéance future.
     */
    public Paiement enregistrer(Long contratId, BigDecimal montant, LocalDate date,
                                ModePaiement mode, StatutPaiement statut) {
        if (contratId == null) throw new BusinessException("Le contrat est obligatoire.");
        if (montant == null || montant.signum() <= 0)
            throw new BusinessException("Le montant doit être strictement positif.");
        if (statut == null) statut = StatutPaiement.EFFECTUE;
        if (statut != StatutPaiement.EFFECTUE && statut != StatutPaiement.EN_ATTENTE)
            throw new BusinessException("Un nouveau paiement est soit EFFECTUE, soit EN_ATTENTE.");
        if (statut == StatutPaiement.EFFECTUE && mode == null)
            throw new BusinessException("Le mode de paiement est obligatoire pour un paiement effectué.");
        if (date == null) date = LocalDate.now();
        if (statut == StatutPaiement.EFFECTUE && date.isAfter(LocalDate.now()))
            throw new BusinessException("Un paiement effectué ne peut pas avoir une date future.");

        Contrat c = contratDao.findByIdComplet(contratId);
        if (c == null) throw new BusinessException("Contrat introuvable : " + contratId);
        if (c.getStatut() == StatutContrat.RESILIE)
            throw new BusinessException("Impossible d'enregistrer un paiement sur un contrat résilié.");

        Paiement p = new Paiement();
        p.setContrat(c);
        p.setMontant(montant);
        p.setDate(date);
        p.setModePaiement(mode);
        p.setStatut(statut);
        paiementDao.save(p);

        if (statut == StatutPaiement.EFFECTUE) activerSiPremierPaiement(c);
        return p;
    }

    // ---------- changement de statut ----------

    /** Valide un paiement en attente. */
    public Paiement valider(Long paiementId, ModePaiement mode) {
        Paiement p = detail(paiementId);
        if (p.getStatut() != StatutPaiement.EN_ATTENTE)
            throw new BusinessException("Seul un paiement EN_ATTENTE peut être validé (statut : " + p.getStatut() + ").");
        ModePaiement m = mode != null ? mode : p.getModePaiement();
        if (m == null) throw new BusinessException("Le mode de paiement est obligatoire pour valider.");

        paiementDao.changerStatut(paiementId, StatutPaiement.EFFECTUE, m);
        activerSiPremierPaiement(p.getContrat());
        return detail(paiementId);
    }

    public Paiement echouer(Long paiementId) {
        return transition(paiementId, StatutPaiement.ECHOUE);
    }

    public Paiement annuler(Long paiementId) {
        return transition(paiementId, StatutPaiement.ANNULE);
    }

    private Paiement transition(Long paiementId, StatutPaiement cible) {
        Paiement p = detail(paiementId);
        if (p.getStatut() != StatutPaiement.EN_ATTENTE)
            throw new BusinessException("Seul un paiement EN_ATTENTE peut passer en " + cible
                    + " (statut : " + p.getStatut() + ").");
        paiementDao.changerStatut(paiementId, cible, null);
        return detail(paiementId);
    }

    // ---------- activation du contrat ----------

    private void activerSiPremierPaiement(Contrat c) {
        if (c.getStatut() == StatutContrat.EN_ATTENTE) {
            contratDao.changerStatut(c.getId(), StatutContrat.ACTIF);
        }
    }
}