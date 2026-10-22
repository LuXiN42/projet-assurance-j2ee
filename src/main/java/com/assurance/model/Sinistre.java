package com.assurance.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name = "sinistres", uniqueConstraints = @UniqueConstraint(name = "uk_sinistre_numero", columnNames = "numero_sinistre"))

public class Sinistre {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @Column(name = "numero_sinistre", nullable = false, unique = true, length = 80) private String numeroSinistre;

    @Column(nullable = false) private LocalDate date;

    @Column(nullable = false, length = 2000) private String description;

    @Column(name = "montant_estime", precision = 12, scale = 2) private BigDecimal montantEstime;

    @Column(name = "type_sinistre", length = 120) private String typeSinistre;

    @Column(name = "pieces_justificatives", length = 2000) private String piecesJustificatives;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) private StatutSinistre statut;

    @Column(length = 2000) private String commentaires;

    @Column(name = "montant_indemnisation", precision = 12, scale = 2) private BigDecimal montantIndemnisation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "contrat_id", nullable = false) private Contrat contrat;

    @Transient public Utilisateur getClient() {return contrat != null ? contrat.getClient() : null;}

    public Sinistre() {}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getNumeroSinistre(){return numeroSinistre;} public void setNumeroSinistre(String v){numeroSinistre=v;}
    public LocalDate getDate(){return date;} public void setDate(LocalDate v){date=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public BigDecimal getMontantEstime(){return montantEstime;} public void setMontantEstime(BigDecimal v){montantEstime=v;}
    public String getTypeSinistre(){return typeSinistre;} public void setTypeSinistre(String v){typeSinistre=v;}
    public String getPiecesJustificatives(){return piecesJustificatives;} public void setPiecesJustificatives(String v){piecesJustificatives=v;}
    public StatutSinistre getStatut(){return statut;} public void setStatut(StatutSinistre v){statut=v;}
    public String getCommentaires(){return commentaires;} public void setCommentaires(String v){commentaires=v;}
    public BigDecimal getMontantIndemnisation(){return montantIndemnisation;} public void setMontantIndemnisation(BigDecimal v){montantIndemnisation=v;}
    public Contrat getContrat(){return contrat;} public void setContrat(Contrat v){contrat=v;}
}
