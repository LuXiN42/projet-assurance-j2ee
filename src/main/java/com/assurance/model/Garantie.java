package com.assurance.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "garanties")

public class Garantie {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @Column(nullable = false, length = 150) private String nom;

    @Column(length = 2000) private String description;

    @ManyToMany(mappedBy = "garanties", fetch = FetchType.LAZY) private List<Contrat> contrats = new ArrayList<>();

    @ManyToMany(mappedBy = "garantiesDisponibles", fetch = FetchType.LAZY) private List<TypeAssurance> typesAssurance = new ArrayList<>();

    public Garantie() {}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getNom(){return nom;} public void setNom(String nom){this.nom=nom;}
    public String getDescription(){return description;} public void setDescription(String description){this.description=description;}
    public List<Contrat> getContrats(){return contrats;} public void setContrats(List<Contrat> contrats){this.contrats=contrats;}
    public List<TypeAssurance> getTypesAssurance(){return typesAssurance;} public void setTypesAssurance(List<TypeAssurance> typesAssurance){this.typesAssurance=typesAssurance;}
}
