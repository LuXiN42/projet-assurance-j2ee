package com.assurance.service;

import com.assurance.dao.ContratDao;
import com.assurance.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service des contrats.
 * activer : EN_ATTENTE ou SUSPENDU vers ACTIF
 * suspendre : ACTIF vers SUSPENDU
 * résilier : ACTIF ou SUSPENDU vers RESILIE (définitif)
 * renouveler : ACTIF ou EXPIRE vers ACTIF, la date de fin est repoussée
 * un contrat actif dont la date de fin est passée devient EXPIRE
 */
public class ContratService {

    private final ContratDao contratDao;

    public ContratService() { this(new ContratDao()); }
    public ContratService(ContratDao contratDao) { this.contratDao = contratDao; }

    // ---------- lecture ----------

    public List<Contrat> rechercher(String numero, String nomClient, Long typeId, StatutContrat statut) {
        return contratDao.rechercher(numero, nomClient, typeId, statut);
    }

    public List<Contrat> contratsDuClient(Long clientId) {
        return contratDao.findByClient(clientId);
    }

    public Contrat detail(Long id) { return mustExist(id); }

    /** Détail complet (client, type, garanties) avec contrôle de propriété. */
    public Contrat detailPourClient(Long contratId, Long clientId) {
        Contrat c = mustExist(contratId);
        if (!c.getClient().getId().equals(clientId)) {
            throw new BusinessException("Ce contrat ne vous appartient pas.");
        }
        return c;
    }

    public boolean estActif(Contrat c) {
        if (c == null || c.getStatut() != StatutContrat.ACTIF) return false;
        LocalDate today = LocalDate.now();
        if (c.getDateDebut() != null && today.isBefore(c.getDateDebut())) return false;
        if (c.getDateFin() != null && today.isAfter(c.getDateFin())) return false;
        return true;
    }

    public Contrat verifierContratActif(Long contratId) {
        Contrat c = mustExist(contratId);
        if (!estActif(c)) {
            throw new BusinessException("Le contrat " + c.getNumeroContrat()
                    + " n'est pas actif (statut = " + c.getStatut() + ").");
        }
        return c;
    }

    public Contrat verifierContratDuClient(Long contratId, Long clientId) {
        Contrat c = contratDao.findByIdEtClient(contratId, clientId);
        if (c == null) throw new BusinessException("Contrat invalide ou n'appartenant pas au client.");
        return c;
    }

    // ---------- création et modification ----------

    public Contrat souscrire(Contrat c) {
        if (c == null) throw new BusinessException("Contrat nul.");
        if (c.getNumeroContrat() == null || c.getNumeroContrat().isBlank()) {
            c.setNumeroContrat(contratDao.genererNumero());
        } else if (contratDao.findByNumero(c.getNumeroContrat()) != null) {
            throw new BusinessException("Le numéro " + c.getNumeroContrat() + " existe déjà.");
        }
        if (c.getClient() == null) throw new BusinessException("Le client est obligatoire.");
        validerChamps(c);
        c.setStatut(StatutContrat.EN_ATTENTE);
        contratDao.save(c);
        return c;
    }

    /** Modifie les champs modifiables. Le client et le numéro ne changent jamais. */
    public Contrat modifier(Long id, LocalDate dateDebut, LocalDate dateFin, BigDecimal prime,
                            TypeAssurance type, List<Garantie> garanties) {
        Contrat c = mustExist(id);
        StatutContrat s = c.getStatut();
        if (s == StatutContrat.RESILIE || s == StatutContrat.EXPIRE) {
            throw new BusinessException("Un contrat " + s + " ne peut plus être modifié.");
        }
        c.setDateDebut(dateDebut);
        c.setDateFin(dateFin);
        c.setPrime(prime);
        c.setTypeAssurance(type);
        c.setGaranties(garanties);
        validerChamps(c);
        contratDao.update(c);
        return c;
    }

    private void validerChamps(Contrat c) {
        if (c.getDateDebut() == null) throw new BusinessException("La date de début est obligatoire.");
        if (c.getDateFin() != null && !c.getDateFin().isAfter(c.getDateDebut())) {
            throw new BusinessException("La date de fin doit être postérieure à la date de début.");
        }
        if (c.getPrime() == null || c.getPrime().signum() <= 0) {
            throw new BusinessException("La prime est obligatoire et doit être strictement positive.");
        }
        if (c.getTypeAssurance() == null) throw new BusinessException("Le type d'assurance est obligatoire.");
    }

    // ---------- transitions ----------

    public Contrat activer(Long contratId) {
        Contrat c = mustExist(contratId);
        exiger(c, "activer", StatutContrat.EN_ATTENTE, StatutContrat.SUSPENDU);
        contratDao.changerStatut(contratId, StatutContrat.ACTIF);
        c.setStatut(StatutContrat.ACTIF);
        return c;
    }

    public Contrat suspendre(Long contratId) {
        Contrat c = mustExist(contratId);
        exiger(c, "suspendre", StatutContrat.ACTIF);
        contratDao.suspendre(contratId);
        c.setStatut(StatutContrat.SUSPENDU);
        return c;
    }

    public Contrat resilier(Long contratId) {
        Contrat c = mustExist(contratId);
        exiger(c, "résilier", StatutContrat.ACTIF, StatutContrat.SUSPENDU);
        contratDao.resilier(contratId);
        c.setStatut(StatutContrat.RESILIE);
        return c;
    }

    public Contrat renouveler(Long contratId, int dureeMois) {
        if (dureeMois <= 0) throw new BusinessException("La durée de renouvellement doit être positive.");
        Contrat c = mustExist(contratId);
        exiger(c, "renouveler", StatutContrat.ACTIF, StatutContrat.EXPIRE);
        LocalDate base = (c.getDateFin() == null || c.getDateFin().isBefore(LocalDate.now()))
                ? LocalDate.now() : c.getDateFin();
        c.setDateFin(base.plusMonths(dureeMois));
        c.setStatut(StatutContrat.ACTIF);
        contratDao.update(c);
        return c;
    }

    public int mettreAJourContratsExpires() { return contratDao.mettreAJourContratsExpires(); }

    // ---------- utilitaires ----------

    private Contrat mustExist(Long id) {
        Contrat c = id == null ? null : contratDao.findByIdComplet(id);
        if (c == null) throw new BusinessException("Contrat introuvable : " + id);
        return c;
    }

    private void exiger(Contrat c, String action, StatutContrat... autorises) {
        for (StatutContrat s : autorises) if (c.getStatut() == s) return;
        throw new BusinessException("Impossible de " + action + " le contrat " + c.getNumeroContrat()
                + " (statut courant : " + c.getStatut() + ").");
    }
}