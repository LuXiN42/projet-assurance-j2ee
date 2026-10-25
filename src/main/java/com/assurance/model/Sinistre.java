package com.assurance.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "sinistres", uniqueConstraints = @UniqueConstraint(name = "uk_sinistre_numero", columnNames = "numero_sinistre"))
public class Sinistre {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @Column(name = "numero_sinistre", nullable = false, unique = true, length = 80) private String numeroSinistre;

    @Column(nullable = false) private LocalDate date;

    @Column(nullable = false, length = 2000) private String description;

    @Column(name = "montant_estime", precision = 12, scale = 2) private BigDecimal montantEstime;

    @Column(name = "type_sinistre", length = 120) private String typeSinistre;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) private StatutSinistre statut;

    @Column(name = "montant_indemnisation", precision = 12, scale = 2) private BigDecimal montantIndemnisation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "contrat_id", nullable = false) private Contrat contrat;

    @Transient public Utilisateur getClient() { return contrat != null ? contrat.getClient() : null; }

    @OneToMany(mappedBy = "sinistre", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) private List<PieceJustificative> pieces = new ArrayList<>();

    @OneToMany(mappedBy = "sinistre", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) @OrderBy("dateCreation ASC") private List<Commentaire> commentaires = new ArrayList<>();

    public Sinistre() {}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getNumeroSinistre(){return numeroSinistre;} public void setNumeroSinistre(String v){numeroSinistre=v;}
    public LocalDate getDate(){return date;} public void setDate(LocalDate v){date=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public BigDecimal getMontantEstime(){return montantEstime;} public void setMontantEstime(BigDecimal v){montantEstime=v;}
    public String getTypeSinistre(){return typeSinistre;} public void setTypeSinistre(String v){typeSinistre=v;}
    public StatutSinistre getStatut(){return statut;} public void setStatut(StatutSinistre v){statut=v;}
    public BigDecimal getMontantIndemnisation(){return montantIndemnisation;} public void setMontantIndemnisation(BigDecimal v){montantIndemnisation=v;}
    public Contrat getContrat(){return contrat;} public void setContrat(Contrat v){contrat=v;}
    public List<PieceJustificative> getPieces() { return pieces; } public void setPieces(List<PieceJustificative> pieces) { this.pieces = pieces; }
    public void ajouterPiece(PieceJustificative p) {pieces.add(p); p.setSinistre(this);}
    public List<Commentaire> getCommentaires() { return commentaires; } public void setCommentaires(List<Commentaire> commentaires) { this.commentaires = commentaires; }
    public void ajouterCommentaire(Commentaire c) {c.setSinistre(this); this.commentaires.add(c);}
}