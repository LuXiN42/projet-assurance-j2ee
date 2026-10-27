package com.assurance.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utilitaire JDBC pur (DriverManager).
 *
 * <p>Le driver MySQL {@code com.mysql.cj.jdbc.Driver} est chargé explicitement
 * via {@link Class#forName(String)} au chargement de la classe.</p>
 *
 * <p>Les paramètres de connexion sont lus depuis le classpath
 * (ordre de priorité) :</p>
 * <ol>
 *   <li>{@code db.properties}, clés : {@code db.url}, {@code db.user}, {@code db.password}</li>
 *   <li>En repli : {@code hibernate.cfg.xml} (propriétés
 *       {@code hibernate.connection.url|username|password})</li>
 *   <li>En dernier repli : valeurs par défaut alignées sur la config Hibernate.</li>
 * </ol>
 *
 * <p>Usage :</p>
 * <pre>{@code
 * try (Connection cn = JdbcUtil.getConnection()) {
 *     // ... requêtes JDBC ...
 * }
 * }</pre>
 */
public final class JdbcUtil {

    /** Driver JDBC MySQL 8.x (aligné sur hibernate.cfg.xml). */
    private static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";

    /** URL JDBC par défaut, alignée sur hibernate.cfg.xml. */
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/assurance_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    /** Utilisateur par défaut, aligné sur hibernate.cfg.xml. */
    private static final String DEFAULT_USER = "assurance";

    /** Mot de passe par défaut, aligné sur hibernate.cfg.xml. */
    private static final String DEFAULT_PASSWORD = "assuranceGroupeO";

    private static String url;
    private static String user;
    private static String password;

    static {
        try {
            // 1) Tentative : db.properties
            loadFromDbProperties();
            // 2) Compléter avec hibernate.cfg.xml si des clés manquent
            if (url == null || url.isEmpty()) {
                loadFromHibernateCfg();
            }
        } catch (Exception ignored) {
            // on continue avec les valeurs par défaut ci-dessous
        }
        if (url == null || url.isEmpty()) {
            url = DEFAULT_URL;
        }
        if (user == null) {
            user = DEFAULT_USER;
        }
        if (password == null) {
            password = DEFAULT_PASSWORD;
        }
        try {
            Class.forName(DEFAULT_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(
                    "Driver JDBC introuvable : " + DEFAULT_DRIVER
                            + " : ajouter mysql-connector-j au pom.xml.");
        }
    }

    private JdbcUtil() {}

    /** Charge db.url/db.user/db.password depuis src/main/resources/db.properties (classpath). */
    private static void loadFromDbProperties() {
        try (InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in == null) {
                return; // fichier absent : on laisse les valeurs par défaut
            }
            Properties p = new Properties();
            p.load(in);
            url      = p.getProperty("db.url",      DEFAULT_URL);
            user     = p.getProperty("db.user",     DEFAULT_USER);
            password = p.getProperty("db.password", DEFAULT_PASSWORD);
        } catch (Exception ignored) {
            // silencieux : on conserve les défauts
        }
    }

    /** Repli : charge l'URL depuis hibernate.cfg.xml. */
    private static void loadFromHibernateCfg() {
        try (InputStream in = openConfigStream()) {
            if (in == null) {
                return;
            }
            org.w3c.dom.Document doc = javax.xml.parsers.DocumentBuilderFactory
                    .newInstance().newDocumentBuilder().parse(in);
            doc.getDocumentElement().normalize();

            String u = readProperty(doc, "hibernate.connection.url");
            String usr = readProperty(doc, "hibernate.connection.username");
            String pwd = readProperty(doc, "hibernate.connection.password");
            if (u != null && !u.isEmpty())  url = u;
            if (usr != null)               user = usr;
            if (pwd != null)               password = pwd;
        } catch (Exception ignored) {
            // silencieux
        }
    }

    private static InputStream openConfigStream() {
        InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("hibernate.cfg.xml");
        if (in != null) return in;
        try {
            java.nio.file.Path p = java.nio.file.Paths.get("src", "main", "resources", "hibernate.cfg.xml");
            if (java.nio.file.Files.exists(p)) {
                return java.nio.file.Files.newInputStream(p);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static String readProperty(org.w3c.dom.Document doc, String name) {
        org.w3c.dom.NodeList nl = doc.getElementsByTagName("property");
        for (int i = 0; i < nl.getLength(); i++) {
            org.w3c.dom.Element e = (org.w3c.dom.Element) nl.item(i);
            if (name.equals(e.getAttribute("name"))) {
                return e.getTextContent().trim();
            }
        }
        return null;
    }

    /**
     * Ouvre (et fait vivre le temps de la requête) une connexion JDBC.
     *
     * @return connexion JDBC ouverte via {@link DriverManager}.
     * @throws SQLException si la connexion échoue.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
