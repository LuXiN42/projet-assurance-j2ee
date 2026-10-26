package com.assurance.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Envoie la conversation à l'API Gemini et renvoie la réponse de l'assistant.
 * La clé API est lue dans src/main/resources/ia.properties,
 * un fichier ignoré par Git : chacun y met sa propre clé.
 */
public class AssistantIAService {

    
    // modèles essayés dans l'ordre : si le premier est surchargé (erreur 503),
    // on réessaie avec le suivant
    private static final String[] MODELES = { "gemini-flash-latest", "gemini-flash-lite-latest" };
    private static final String URL_API = "https://generativelanguage.googleapis.com/v1beta/models/";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * @param consignes  le texte qui explique à l'IA son rôle (+ les infos du client)
     * @param historique les messages déjà échangés : {"user", texte} ou {"model", texte}
     * @return la réponse de l'assistant
     */
    public String repondre(String consignes, List<String[]> historique) {
        String cle = lireCle();
        if (cle == null || cle.isBlank()) {
            throw new BusinessException("L'assistant n'est pas configuré (clé API manquante).");
        }

        // corps de la requête au format attendu par Gemini
        JsonObject corps = new JsonObject();

        JsonObject systeme = new JsonObject();
        systeme.add("parts", texte(consignes));
        corps.add("system_instruction", systeme);

        JsonArray contents = new JsonArray();
        for (String[] message : historique) {
            JsonObject m = new JsonObject();
            m.addProperty("role", message[0]);
            m.add("parts", texte(message[1]));
            contents.add(m);
        }
        corps.add("contents", contents);

        HttpResponse<String> reponse = null;
        for (String modele : MODELES) {
            HttpRequest requete = HttpRequest.newBuilder(URI.create(URL_API + modele + ":generateContent"))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", cle)
                    .POST(HttpRequest.BodyPublishers.ofString(corps.toString()))
                    .build();
            try {
                reponse = http.send(requete, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                // pas de réponse (trop long ou connexion coupée) : on essaie le modèle suivant
                System.err.println("Modèle " + modele + " injoignable : " + e);
                reponse = null;
                continue;
            }
            if (reponse.statusCode() != 503) {
                break; // pas surchargé : on garde cette réponse
            }
            System.err.println("Modèle " + modele + " surchargé (503), on essaie le suivant.");
        }

        if (reponse == null) {
            throw new BusinessException("Impossible de joindre l'assistant. Réessayez plus tard.");
        }

        if (reponse.statusCode() != 200) {
            // on garde le détail dans la console de Tomcat pour comprendre le problème
            System.err.println("Erreur Gemini " + reponse.statusCode() + " : " + reponse.body());
            throw new BusinessException("L'assistant est indisponible pour le moment.");
        }
        // la réponse se trouve dans candidates[0].content.parts[0].text
        try {
            JsonObject json = JsonParser.parseString(reponse.body()).getAsJsonObject();
            return json.getAsJsonArray("candidates").get(0).getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts").get(0).getAsJsonObject()
                    .get("text").getAsString();
        } catch (RuntimeException e) {
            System.err.println("Réponse Gemini illisible : " + reponse.body());
            throw new BusinessException("L'assistant n'a pas pu répondre à cette question.");
        }
    }

    // lit la clé dans ia.properties (si le fichier n'existe pas, on regarde
    // la variable d'environnement GEMINI_API_KEY, utile pour le correcteur)
    private String lireCle() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("ia.properties")) {
            if (in != null) {
                Properties config = new Properties();
                config.load(in);
                String cle = config.getProperty("gemini.api.key");
                if (cle != null && !cle.isBlank()) {
                    return cle.trim();
                }
            }
        } catch (IOException e) {
            System.err.println("Impossible de lire ia.properties : " + e.getMessage());
        }
        return System.getenv("GEMINI_API_KEY");
    }

    // Gemini attend le texte sous la forme "parts": [ { "text": "..." } ]
    private JsonArray texte(String contenu) {
        JsonObject part = new JsonObject();
        part.addProperty("text", contenu);
        JsonArray parts = new JsonArray();
        parts.add(part);
        return parts;
    }
}