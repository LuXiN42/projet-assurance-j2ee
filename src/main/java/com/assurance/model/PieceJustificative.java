package com.assurance.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pieces_justificatives")
public class PieceJustificative {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @Column(name = "nom_original", nullable = false, length = 255) private String nomOriginal;

    @Column(name = "nom_stocke", nullable = false, unique = true, length = 300) private String nomStocke;

    @Column(name = "type_mime", length = 100) private String typeMime; 

    private long taille;

    @Column(name = "date_depot", nullable = false) private LocalDateTime dateDepot = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sinistre_id", nullable = false) private Sinistre sinistre;

    public PieceJustificative() {}

    public Long getId(){return id;}
    public String getNomOriginal(){return nomOriginal;} public void setNomOriginal(String v){nomOriginal=v;}
    public String getNomStocke(){return nomStocke;} public void setNomStocke(String v){nomStocke=v;}
    public String getTypeMime(){return typeMime;} public void setTypeMime(String v){typeMime=v;}
    public long getTaille(){return taille;} public void setTaille(long v){taille=v;}
    public LocalDateTime getDateDepot(){return dateDepot;}
    public Sinistre getSinistre(){return sinistre;} public void setSinistre(Sinistre v){sinistre=v;}
}