package com.assurance.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Commentaire écrit par un agent d'assurance sur un sinistre.
 * Les commentaires sont ordonnés par date de création croissante.
 */
@Entity
@Table(name = "commentaires")
public class Commentaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @Column(nullable = false, length = 2000) private String texte;

    @Column(name = "date_creation", nullable = false) private LocalDateTime dateCreation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auteur_id", nullable = false) private Utilisateur auteur;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sinistre_id", nullable = false) private Sinistre sinistre;

    public Commentaire() {}

    @PrePersist
    public void prePersist() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTexte() { return texte; }
    public void setTexte(String texte) { this.texte = texte; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public Utilisateur getAuteur() { return auteur; }
    public void setAuteur(Utilisateur auteur) { this.auteur = auteur; }

    public Sinistre getSinistre() { return sinistre; }
    public void setSinistre(Sinistre sinistre) { this.sinistre = sinistre; }
}
