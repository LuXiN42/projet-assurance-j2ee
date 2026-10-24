package com.assurance.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity @Table(name = "types_assurance")
public class TypeAssurance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @Column(nullable = false, length = 150) private String nom;

    @Column(length = 2000) private String description;

    @Column(name = "prix_base", precision = 12, scale = 2) private BigDecimal prixBase;

    @OneToMany(mappedBy = "typeAssurance", fetch = FetchType.LAZY) private Set<Contrat> contrats = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.LAZY) @JoinTable(name = "type_assurance_garanties", joinColumns = @JoinColumn(name = "type_assurance_id"), inverseJoinColumns = @JoinColumn(name = "garantie_id"))

    private Set<Garantie> garantiesDisponibles = new LinkedHashSet<>();

    public TypeAssurance() {}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getNom(){return nom;} public void setNom(String nom){this.nom=nom;}
    public String getDescription(){return description;} public void setDescription(String description){this.description=description;}
    public BigDecimal getPrixBase(){return prixBase;} public void setPrixBase(BigDecimal prixBase){this.prixBase=prixBase;}
    public Set<Contrat> getContrats(){return contrats;} public void setContrats(Set<Contrat> contrats){this.contrats=contrats;}
    public Set<Garantie> getGarantiesDisponibles(){return garantiesDisponibles;} public void setGarantiesDisponibles(Set<Garantie> garantiesDisponibles){this.garantiesDisponibles=garantiesDisponibles;}
}
