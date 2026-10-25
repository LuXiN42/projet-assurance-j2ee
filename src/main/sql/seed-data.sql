-- =============================================================================
-- seed-data.sql — Jeu de données de test (assurance J2EE 2026-2027)
-- =============================================================================
-- Moteur cible : MySQL 8.x / MariaDB 10.x (charset utf8mb4)
--
-- IMPORTANT — SCHÉMA
-- La table `utilisateurs` est créée automatiquement par Hibernate au démarrage
-- de l'application à partir de l'entité JPA `com.assurance.model.Utilisateur`
-- (annotation @Table(name = "utilisateurs")). Ce script NE CONTIENT AUCUN
-- CREATE TABLE : la table et ses index sont supposés déjà présents.
--
-- Colonnes retenues (correspondance exacte avec l'entité Utilisateur) :
--   id                  BIGINT          PK, auto-incrémenté (géré par Hibernate)
--   email               VARCHAR(255)    UNIQUE, NOT NULL
--   mot_de_passe_hash   VARCHAR(255)    NOT NULL
--   role                VARCHAR(32)     NOT NULL  (enum Role stocké en STRING)
--   nom                 VARCHAR(100)
--   prenom              VARCHAR(100)
--   date_inscription    DATE
--
-- Colonnes PRÉCISÉMENT EXCLUES du seed (non conformes à l'entité) :
--   telephone, date_naissance, adresse, ville, code_postal
--
-- MOT DE PASSE — HASH BCrypt
-- Tous les comptes du jeu d'essai partagent le même hash BCrypt (cost=10,
-- préfixe $2a$) qui valide le mot de passe en clair :
--     "Password123!"
-- Le hash a été généré via Python `bcrypt.hashpw(b"Password123!",
-- bcrypt.gensalt(rounds=10, prefix=b"2a"))` et vérifié avec
-- `bcrypt.checkpw(b"Password123!", hash)` -> True.
--
-- VALEURS DE role (énumération Java Role, stockée en chaîne via
-- @Enumerated(EnumType.STRING)) :
--   CLIENT, AGENT, ADMIN   (toutes en MAJUSCULES)
--
-- DOUBLONS — MySQL
-- INSERT IGNORE est utilisé pour neutraliser les violations de la contrainte
-- UNIQUE sur `email` en cas de ré-exécution du script.
--
-- =============================================================================
-- 0. NETTOYAGE (optionnel — décommenter pour repartir de zéro)
-- =============================================================================
-- DELETE FROM utilisateurs WHERE email LIKE '%@assurance.test'
--                            OR email LIKE '%@assurance.fr';

-- =============================================================================
-- 1. JEU DE DONNÉES — 25 CLIENTS + 4 AGENTS + 1 ADMIN
-- =============================================================================
-- Hash BCrypt partagé ($2a$10$ cost=10) pour le mot de passe "Password123!" :
--   $2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS

INSERT IGNORE INTO utilisateurs
    (email, mot_de_passe_hash, role, nom, prenom, date_inscription)
VALUES
-- ---------- 25 CLIENTS (role = CLIENT, domaine @assurance.test) ----------
('martin.alice@assurance.test',    '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Martin',     'Alice',    '2024-01-15'),
('dubois.bruno@assurance.test',    '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Dubois',     'Bruno',    '2024-01-20'),
('bernard.chloe@assurance.test',   '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Bernard',    'Chloé',    '2024-02-03'),
('petit.david@assurance.test',     '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Petit',      'David',    '2024-02-10'),
('robert.emilie@assurance.test',   '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Robert',     'Émilie',   '2024-02-14'),
('richard.fabien@assurance.test',  '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Richard',    'Fabien',   '2024-03-01'),
('durand.gabrielle@assurance.test','$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Durand',     'Gabrielle','2024-03-08'),
('moreau.hugo@assurance.test',     '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Moreau',     'Hugo',     '2024-03-12'),
('laurent.isabelle@assurance.test','$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Laurent',    'Isabelle', '2024-03-15'),
('simon.julien@assurance.test',    '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Simon',      'Julien',   '2024-03-20'),
('michel.karine@assurance.test',   '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Michel',     'Karine',   '2024-04-02'),
('lefebvre.luc@assurance.test',    '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Lefebvre',   'Luc',      '2024-04-10'),
('leroy.marie@assurance.test',     '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Leroy',      'Marie',    '2024-04-15'),
('roux.nicolas@assurance.test',    '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Roux',       'Nicolas',  '2024-04-20'),
('vincent.olivia@assurance.test',  '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Vincent',    'Olivia',   '2024-05-01'),
('fournier.paul@assurance.test',   '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Fournier',   'Paul',     '2024-05-05'),
('garcia.quentin@assurance.test',  '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Garcia',     'Quentin',  '2024-05-09'),
('andre.rachel@assurance.test',    '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'André',      'Rachel',   '2024-05-12'),
('mercier.sophie@assurance.test',  '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Mercier',    'Sophie',   '2024-05-18'),
('blanc.thomas@assurance.test',    '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Blanc',      'Thomas',   '2024-05-22'),
('guerin.ursula@assurance.test',   '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Guérin',     'Ursula',   '2024-06-01'),
('boyer.vincent@assurance.test',   '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Boyer',      'Vincent',  '2024-06-04'),
('garnier.william@assurance.test', '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Garnier',    'William',  '2024-06-08'),
('chevalier.xavier@assurance.test','$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'Chevalier',  'Xavier',   '2024-06-12'),
('francois.yasmine@assurance.test','$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'CLIENT', 'François',   'Yasmine',  '2024-06-18'),

-- ---------- 4 AGENTS (role = AGENT, domaine @assurance.fr) ----------
('agent.dupont@assurance.fr',      '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'AGENT',  'Dupont',     'Jean-Pierre','2023-09-01'),
('agent.lefort@assurance.fr',      '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'AGENT',  'Lefort',     'Catherine', '2023-09-01'),
('agent.mercier@assurance.fr',     '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'AGENT',  'Mercier',    'Antoine',   '2023-09-15'),
('agent.beaumont@assurance.fr',    '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'AGENT',  'Beaumont',   'Sophie',    '2023-10-01'),

-- ---------- 1 ADMIN (role = ADMIN) ----------
('admin@assurance.fr',             '$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS', 'ADMIN',  'Admin',      'Système',   '2023-01-01');

-- =============================================================================
-- 2. REQUÊTES DE VÉRIFICATION (à exécuter après le seed)
-- =============================================================================
-- a) Effectif total et répartition par rôle :
--      SELECT role, COUNT(*) AS effectif
--        FROM utilisateurs
--       GROUP BY role
--       ORDER BY role;
--    Attendu : ADMIN=1, AGENT=4, CLIENT=25 (total = 30).
--
-- b) Contrôle spécifique du nombre de clients :
--      SELECT COUNT(*) AS nb_clients FROM utilisateurs WHERE role = 'CLIENT';
--    Attendu : 25.
--
-- c) Contrôle spécifique du nombre d'agents :
--      SELECT COUNT(*) AS nb_agents FROM utilisateurs WHERE role = 'AGENT';
--    Attendu : 4.
--
-- d) Contrôle spécifique du nombre d'administrateurs :
--      SELECT COUNT(*) AS nb_admins FROM utilisateurs WHERE role = 'ADMIN';
--    Attendu : 1.
--
-- e) Recherche d'un client par nom (LIKE insensible à la casse) :
--      SELECT id, email, nom, prenom, date_inscription
--        FROM utilisateurs
--       WHERE role = 'CLIENT'
--         AND LOWER(nom) LIKE LOWER('%martin%')
--       ORDER BY nom, prenom;
--
-- f) Recherche d'un client par prénom :
--      SELECT id, email, nom, prenom, date_inscription
--        FROM utilisateurs
--       WHERE role = 'CLIENT'
--         AND LOWER(prenom) LIKE LOWER('%alice%')
--       ORDER BY nom, prenom;
--
-- g) Vérification qu'aucune colonne exclue n'est utilisée :
--      SELECT email, role FROM utilisateurs LIMIT 5;
--    Les colonnes telephone, date_naissance, adresse, ville, code_postal
--    ne doivent PAS apparaître dans le SELECT ci-dessus (elles n'existent
--    pas dans l'entité Utilisateur).
--
-- h) Validation hors-SQL du hash BCrypt (Python, côté poste de dev) :
--      import bcrypt
--      h = b"$2a$10$kV8ub5mb6P1.AHH5wUJvQeyx3TI4N1yFmqZCcOHSfoJ83uiDPvlKS"
--      assert bcrypt.checkpw(b"Password123!", h) is True
-- =============================================================================
