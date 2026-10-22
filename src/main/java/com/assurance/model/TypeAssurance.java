package com.assurance.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "types_assurance")
public class TypeAssurance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @Column(nullable = false, length = 150) private String nom;

    @Column(length = 2000) private String description;

    @Column(name = "prix_base", precision = 12, scale = 2) private BigDecimal prixBase;

    @OneToMany(mappedBy = "typeAssurance", fetch = FetchType.LAZY) private List<Contrat> contrats = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY) @JoinTable(name = "type_assurance_garanties", joinColumns = @JoinColumn(name = "type_assurance_id"), inverseJoinColumns = @JoinColumn(name = "garantie_id"))

    private List<Garantie> garantiesDisponibles = new ArrayList<>();

    public TypeAssurance() {}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getNom(){return nom;} public void setNom(String nom){this.nom=nom;}
    public String getDescription(){return description;} public void setDescription(String description){this.description=description;}
    public BigDecimal getPrixBase(){return prixBase;} public void setPrixBase(BigDecimal prixBase){this.prixBase=prixBase;}
    public List<Contrat> getContrats(){return contrats;} public void setContrats(List<Contrat> contrats){this.contrats=contrats;}
    public List<Garantie> getGarantiesDisponibles(){return garantiesDisponibles;} public void setGarantiesDisponibles(List<Garantie> garantiesDisponibles){this.garantiesDisponibles=garantiesDisponibles;}
}
