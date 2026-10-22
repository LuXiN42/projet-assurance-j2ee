package com.assurance.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "contrats", uniqueConstraints = @UniqueConstraint(name = "uk_contrat_numero", columnNames = "numero_contrat"))
public class Contrat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "numero_contrat", nullable = false, unique = true, length = 80) private String numeroContrat;
    @Column(name = "date_debut", nullable = false) private LocalDate dateDebut;
    @Column(name = "date_fin") private LocalDate dateFin;
    @Column(precision = 12, scale = 2) private BigDecimal prime;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) private StatutContrat statut;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "client_id", nullable = false) private Utilisateur client;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "type_assurance_id", nullable = false) private TypeAssurance typeAssurance;
    @ManyToMany(fetch = FetchType.LAZY) @JoinTable(name = "contrat_garanties", joinColumns = @JoinColumn(name = "contrat_id"), inverseJoinColumns = @JoinColumn(name = "garantie_id")) private List<Garantie> garanties = new ArrayList<>();
    @OneToMany(mappedBy = "contrat", fetch = FetchType.LAZY) private List<Sinistre> sinistres = new ArrayList<>();
    @OneToMany(mappedBy = "contrat", fetch = FetchType.LAZY) private List<Paiement> paiements = new ArrayList<>();
    public Contrat() {}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getNumeroContrat(){return numeroContrat;} public void setNumeroContrat(String v){numeroContrat=v;}
    public LocalDate getDateDebut(){return dateDebut;} public void setDateDebut(LocalDate v){dateDebut=v;}
    public LocalDate getDateFin(){return dateFin;} public void setDateFin(LocalDate v){dateFin=v;}
    public BigDecimal getPrime(){return prime;} public void setPrime(BigDecimal v){prime=v;}
    public StatutContrat getStatut(){return statut;} public void setStatut(StatutContrat v){statut=v;}
    public Utilisateur getClient(){return client;} public void setClient(Utilisateur v){client=v;}
    public TypeAssurance getTypeAssurance(){return typeAssurance;} public void setTypeAssurance(TypeAssurance v){typeAssurance=v;}
    public List<Garantie> getGaranties(){return garanties;} public void setGaranties(List<Garantie> v){garanties=v;}
    public List<Sinistre> getSinistres(){return sinistres;} public void setSinistres(List<Sinistre> v){sinistres=v;}
    public List<Paiement> getPaiements(){return paiements;} public void setPaiements(List<Paiement> v){paiements=v;}
}
