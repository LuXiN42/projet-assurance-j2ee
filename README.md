# B'Care : plateforme de gestion d'assurance (version J2EE)

Projet J2EE réalisé en binôme (Jilani Mohamed et Loïc Robert), ING2 CY Tech, 2026-2027.

B'Care est une application web de gestion d'une compagnie d'assurance avec trois profils : client, agent et administrateur. Cette version utilise les Servlets, les JSP et Hibernate. La version Spring Boot du projet est dans un dépôt séparé.

**Technologies :** Java 17, Maven ; Jakarta Servlets, JSP, JSTL ; Hibernate 6 (et JDBC pour les statistiques) ; MySQL 8 ; Apache Tomcat 10.1 ; Chart.js pour les graphiques ; API Gemini pour l'assistant Bibi.

## Lancer le projet

### 1. Prérequis

- JDK 17
- Maven 3.8 ou plus
- MySQL 8
- Apache Tomcat 10.1 (Tomcat 9 ne marche pas, le projet utilise Jakarta)

### 2. Récupérer le projet

```bash
git clone https://github.com/LuXiN42/projet-assurance-j2ee.git
cd projet-assurance-j2ee
```

### 3. Créer la base de données

#### 3.1 Vérifier ou démarrer MySQL

- **Linux :**
  ```bash
  sudo systemctl start mysql
  ```
- **Windows :** démarrer le service **MySQL80** dans `services.msc`.
- **Mac :**
  ```bash
  brew services start mysql
  ```

#### 3.2 Se connecter en root

- **Linux / Mac :**
  ```bash
  sudo mysql -u root -p
  ```
- **Windows :**
  ```bash
  mysql -u root -p
  ```

Saisir le mot de passe root lorsqu'il est demandé. Les commandes SQL suivantes sont à exécuter à l'invite `mysql>`.

#### 3.3 Créer la base et l'utilisateur

À l'invite `mysql>`, exécuter :

```sql
CREATE DATABASE assurance_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'assurance'@'localhost' IDENTIFIED BY 'Assurance2026!';
GRANT ALL PRIVILEGES ON assurance_db.* TO 'assurance'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

Le mot de passe `Assurance2026!` est choisi pour respecter la politique `validate_password` de MySQL 8 (longueur, majuscule, minuscule, chiffre et caractère spécial).

#### 3.4 Tester la connexion de l'utilisateur

Dans un terminal, et non à l'invite `mysql>`, exécuter :

```bash
mysql -u assurance -p assurance_db
```

Saisir `Assurance2026!` lorsque le mot de passe est demandé. À l'invite `mysql>`, vérifier l'accès puis quitter :

```sql
SHOW TABLES;
EXIT;
```

#### 3.5 Charger le schéma puis les données

Depuis la racine du projet, dans le **terminal** (pas dans le client MySQL), exécuter les commandes dans cet ordre :

```bash
mysql -u assurance -p assurance_db < src/main/sql/schema.sql
mysql -u assurance -p assurance_db < src/main/sql/data.sql
```

Saisir `Assurance2026!` chaque fois que le terminal le demande.

> **Attention :** si vous voyez l'invite `->` ou `>` après avoir tapé une commande, vous êtes encore dans le client MySQL. Tapez `\c` pour annuler la commande en cours, puis `EXIT;` pour quitter. Les commandes de chargement ci-dessus se lancent depuis le terminal, à la racine du projet.

#### 3.6 Vérifier les données chargées

Se connecter à la base, puis compter les utilisateurs :

```bash
mysql -u assurance -p assurance_db
```

À l'invite `mysql>`, exécuter :

```sql
SELECT COUNT(*) FROM utilisateur;
EXIT;
```

Le résultat attendu est **30**.

#### 3.7 Configurer les identifiants de connexion

Vérifier que les paramètres de connexion à MySQL utilisent la base `assurance_db`, l'utilisateur `assurance` et le mot de passe `Assurance2026!` dans les deux fichiers suivants :

- `src/main/resources/hibernate.cfg.xml`
- `db.properties`

Si vous changez d'utilisateur MySQL ou de mot de passe, mettez à jour les paramètres correspondants dans ces deux fichiers.

#### 3.8 Erreurs courantes

| Message d'erreur | Cause possible / solution |
|---|---|
| `Your password does not satisfy the current policy requirements` | Mot de passe trop simple : respecter au moins 8 caractères, avec majuscule, minuscule, chiffre et caractère spécial. Utiliser `Assurance2026!`. |
| `Access denied for user` | Mot de passe incorrect ou utilisateur non créé. Vérifier les identifiants et la création de l'utilisateur. |
| `Unknown database` | La base de données n'a pas été créée. Reprendre l'étape 3.3. |
| `Can't connect to MySQL server` | Le service MySQL est arrêté. Le démarrer selon le système d'exploitation (étape 3.1). |
| `No such file or directory` | Le terminal n'est pas dans le bon dossier. Se placer à la racine du projet. |

### 4. Clé de l'assistant Bibi (facultatif)

Bibi utilise l'API Gemini. Sans clé, toute l'application marche sauf Bibi, qui affiche un message d'indisponibilité.

1. Créer une clé gratuite sur <https://aistudio.google.com/apikey>.
2. Copier `src/main/resources/ia.properties.example` en `ia.properties` dans le même dossier.
3. Remplacer `COLLE_TA_CLE_ICI` par la clé.

`ia.properties` est dans le `.gitignore`, la clé ne part jamais sur GitHub.

### 5. Compiler et déployer

Compiler le projet :

```bash
mvn clean package
```

Le fichier produit est `target/assurance.war`. Définir `TOMCAT_HOME` avec le chemin vers le dossier d'installation de Tomcat, puis copier le WAR dans son dossier `webapps` et démarrer Tomcat.

- **Linux / Mac :**
  ```bash
  cp target/assurance.war $TOMCAT_HOME/webapps/
  $TOMCAT_HOME/bin/startup.sh
  ```
- **Windows (cmd) :** définir `CATALINA_HOME` avec le chemin vers le dossier d'installation de Tomcat, puis exécuter :
  ```cmd
  copy target\assurance.war %CATALINA_HOME%\webapps\
  %CATALINA_HOME%\bin\startup.bat
  ```

Pour un redéploiement propre, supprimer l'ancien dossier `webapps/assurance/` et l'ancien fichier `webapps/assurance.war` avant de recopier le nouveau WAR. Les logs sont disponibles dans `logs/catalina.out`. Pour arrêter Tomcat, utiliser `shutdown.sh` (Linux / Mac) ou `shutdown.bat` (Windows), dans le dossier `bin` de Tomcat.

L'application est disponible sur <http://localhost:8080/assurance>.

## Comptes de test

Tous les comptes de test ont le mot de passe `Password123!`.

| Profil | Identifiant |
|---|---|
| Administrateur | `admin@assurance.fr` |
| Agent | `agent.dupont@assurance.fr` |
| Client | `durand.gabrielle@assurance.test` |

Le jeu de données contient 1 admin, 4 agents et 25 clients avec leurs contrats, sinistres et paiements. On peut aussi créer un compte client depuis la page Inscription.

## Fonctionnalités

### Client

- Consulter ses contrats et leurs garanties
- Déclarer un sinistre avec pièces justificatives et suivre son avancement
- Voir l'historique de ses paiements
- Modifier son profil et son mot de passe
- Poser ses questions à Bibi, l'assistant qui connaît ses contrats et ses sinistres

### Agent

- Consulter les clients
- Créer, modifier et rechercher des contrats
- Gérer le catalogue des garanties
- Traiter les sinistres (analyse, acceptation, refus, indemnisation, commentaires)
- Enregistrer les paiements
- Consulter un tableau de bord avec statistiques

### Administrateur

- Tout ce que fait un agent
- Gérer les utilisateurs (clients, agents, administrateurs)
- Gérer les types d'assurance
- Consulter les statistiques globales

## Organisation du code

```text
src/main/java/com/assurance/
├── controller/   servlets, rangées par profil (client, agent, admin)
├── dao/          accès aux données (Hibernate, JDBC pour les stats)
├── filter/       contrôle d'accès selon le rôle
├── model/        entités JPA
├── service/      règles métier et appel à Gemini
└── util/         validation des formulaires, hash des mots de passe...

src/main/webapp/
├── WEB-INF/views/   pages JSP par profil + fragments communs
├── css/ js/ fonts/

src/main/sql/        scripts de la base
docs/                MCD
```

## Sécurité

- Mots de passe hachés avec BCrypt, jamais stockés en clair
- Filtre qui vérifie le rôle avant chaque page protégée (403 sinon)
- Validation côté serveur de tous les formulaires
- Échappement des données affichées (protection contre le XSS)
- Pages d'erreur personnalisées (403, 404, 500)

## Modèle de données

Le MCD est dans `docs/mcd_B.CAREFULL.png`.
