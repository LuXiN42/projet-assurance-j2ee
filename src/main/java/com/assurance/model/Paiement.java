package com.assurance.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name = "paiements")

public class Paiement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal montant;

    @Column(nullable = false) private LocalDate date;

    @Enumerated(EnumType.STRING) @Column(name = "mode_paiement", length = 32) private ModePaiement modePaiement;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) private StatutPaiement statut;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "contrat_id", nullable = false) private Contrat contrat;

    public Paiement() {}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public BigDecimal getMontant(){return montant;} public void setMontant(BigDecimal v){montant=v;}
    public LocalDate getDate(){return date;} public void setDate(LocalDate v){date=v;}
    public ModePaiement getModePaiement(){return modePaiement;} public void setModePaiement(ModePaiement v){modePaiement=v;}
    public StatutPaiement getStatut(){return statut;} public void setStatut(StatutPaiement v){statut=v;}
    public Contrat getContrat(){return contrat;} public void setContrat(Contrat v){contrat=v;}
}
