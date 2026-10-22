-- contrats
ALTER TABLE contrats MODIFY prime DECIMAL(12,2) NOT NULL;
ALTER TABLE contrats ADD CONSTRAINT chk_contrat_dates
    CHECK (date_fin IS NULL OR date_fin > date_debut);
ALTER TABLE contrats ADD CONSTRAINT chk_contrat_prime CHECK (prime > 0);

-- paiements
ALTER TABLE paiements ADD CONSTRAINT chk_paiement_montant CHECK (montant > 0);

-- sinistres
ALTER TABLE sinistres MODIFY description VARCHAR(2000) NOT NULL;
ALTER TABLE sinistres ADD CONSTRAINT chk_sinistre_montant
    CHECK (montant_estime IS NULL OR montant_estime >= 0);
ALTER TABLE sinistres ADD CONSTRAINT chk_sinistre_indemn
    CHECK (montant_indemnisation IS NULL OR montant_indemnisation >= 0);
ALTER TABLE sinistres ADD CONSTRAINT chk_sinistre_indemn_statut
    CHECK ((montant_indemnisation IS NULL OR statut IN ('ACCEPTE','INDEMNISE'))
       AND (statut <> 'INDEMNISE' OR montant_indemnisation IS NOT NULL));