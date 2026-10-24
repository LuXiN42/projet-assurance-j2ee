-- ==================
-- Contraintes métier
-- ==================


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

-- ---------------------------------------------------------------------
-- Règle métier (point 2 du sujet) :
--   * montant_indemnisation n'est autorisé que pour les statuts ACCEPTE
--     ou INDEMNISE ;
--   * si statut = INDEMNISE, montant_indemnisation est obligatoire.
-- ---------------------------------------------------------------------
ALTER TABLE sinistres ADD CONSTRAINT chk_sinistre_indemn_statut
    CHECK ((montant_indemnisation IS NULL OR statut IN ('ACCEPTE','INDEMNISE'))
       AND (statut <> 'INDEMNISE' OR montant_indemnisation IS NOT NULL));

-- ---------------------------------------------------------------------
-- Table commentaires
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS commentaires (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    texte           VARCHAR(2000) NOT NULL,
    date_creation   DATETIME     NOT NULL,
    auteur_id       BIGINT       NOT NULL,
    sinistre_id     BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_commentaire_auteur   FOREIGN KEY (auteur_id)   REFERENCES utilisateurs(id),
    CONSTRAINT fk_commentaire_sinistre FOREIGN KEY (sinistre_id) REFERENCES sinistres(id),
    CONSTRAINT chk_commentaire_texte   CHECK (CHAR_LENGTH(TRIM(texte)) >= 1 AND CHAR_LENGTH(texte) <= 2000)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_commentaire_sinistre   ON commentaires (sinistre_id, date_creation);
CREATE INDEX idx_commentaire_auteur     ON commentaires (auteur_id);

-- ---------------------------------------------------------------------
-- Table de jointure TypeAssurance <-> Garantie (déjà utilisée par
-- l'annotation @JoinTable de TypeAssurance.garantiesDisponibles)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS type_assurance_garanties (
    type_assurance_id BIGINT NOT NULL,
    garantie_id       BIGINT NOT NULL,
    PRIMARY KEY (type_assurance_id, garantie_id),
    CONSTRAINT fk_tag_type   FOREIGN KEY (type_assurance_id) REFERENCES types_assurance(id),
    CONSTRAINT fk_tag_garant FOREIGN KEY (garantie_id)       REFERENCES garanties(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;