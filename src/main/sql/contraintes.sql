-- ==================
-- Contraintes métier
-- À exécuter UNE SEULE FOIS, après le premier démarrage de l'application
-- (Hibernate doit avoir créé les tables) et avant le seed.
-- Nécessite MySQL >= 8.0.16
-- ==================

-- contrats
ALTER TABLE contrats ADD CONSTRAINT chk_contrat_dates
    CHECK (date_fin IS NULL OR date_fin > date_debut);
ALTER TABLE contrats ADD CONSTRAINT chk_contrat_prime
    CHECK (prime > 0);

-- paiements
ALTER TABLE paiements ADD CONSTRAINT chk_paiement_montant
    CHECK (montant > 0);

-- sinistres
ALTER TABLE sinistres ADD CONSTRAINT chk_sinistre_montant
    CHECK (montant_estime IS NULL OR montant_estime >= 0);
ALTER TABLE sinistres ADD CONSTRAINT chk_sinistre_indemn
    CHECK (montant_indemnisation IS NULL OR montant_indemnisation >= 0);

-- Règle métier :
--  * montant_indemnisation seulement pour ACCEPTE ou INDEMNISE ;
--  * statut INDEMNISE => montant_indemnisation obligatoire.
ALTER TABLE sinistres ADD CONSTRAINT chk_sinistre_indemn_statut
    CHECK ((montant_indemnisation IS NULL OR statut IN ('ACCEPTE','INDEMNISE'))
       AND (statut <> 'INDEMNISE' OR montant_indemnisation IS NOT NULL));

-- commentaires (table déjà créée par Hibernate : on ajoute seulement la règle et les index)
ALTER TABLE commentaires ADD CONSTRAINT chk_commentaire_texte
    CHECK (CHAR_LENGTH(TRIM(texte)) >= 1 AND CHAR_LENGTH(texte) <= 2000);
CREATE INDEX idx_commentaire_sinistre ON commentaires (sinistre_id, date_creation);
CREATE INDEX idx_commentaire_auteur   ON commentaires (auteur_id);