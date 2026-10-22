package com.assurance.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "utilisateurs", uniqueConstraints = @UniqueConstraint(name = "uk_utilisateur_email", columnNames = "email"))
public class Utilisateur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    @Column(name = "mot_de_passe_hash", nullable = false, length = 255)
    private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32)
    private Role role;
    @Column(length = 100) private String nom;
    @Column(length = 100) private String prenom;
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Contrat> contrats = new ArrayList<>();
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Sinistre> sinistres = new ArrayList<>();
    public Utilisateur() {}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getEmail(){return email;} public void setEmail(String email){this.email=email;}
    public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String passwordHash){this.passwordHash=passwordHash;}
    public Role getRole(){return role;} public void setRole(Role role){this.role=role;}
    public String getNom(){return nom;} public void setNom(String nom){this.nom=nom;}
    public String getPrenom(){return prenom;} public void setPrenom(String prenom){this.prenom=prenom;}
    public List<Contrat> getContrats(){return contrats;} public void setContrats(List<Contrat> contrats){this.contrats=contrats;}
    public List<Sinistre> getSinistres(){return sinistres;} public void setSinistres(List<Sinistre> sinistres){this.sinistres=sinistres;}
}
