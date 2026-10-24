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

    @Column(name = "pieces_justificatives", length = 2000) private String piecesJustificatives;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) private StatutSinistre statut;

    @Column(length = 2000) private String commentaires;

    @Column(name = "montant_indemnisation", precision = 12, scale = 2) private BigDecimal montantIndemnisation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "contrat_id", nullable = false) private Contrat contrat;

    @OneToMany(mappedBy = "sinistre", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) private List<PieceJustificative> pieces = new ArrayList<>();

    @OneToMany(mappedBy = "sinistre", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("dateCreation ASC") private List<Commentaire> commentairesEntities = new ArrayList<>();

    public Sinistre() {}

    @Transient public Utilisateur getClient() { return contrat != null ? contrat.getClient() : null; }

    public void ajouterPiece(PieceJustificative p) { pieces.add(p); p.setSinistre(this); }
    public void ajouterCommentaire(Commentaire c) { commentairesEntities.add(c); c.setSinistre(this); }

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getNumeroSinistre() { return numeroSinistre; } public void setNumeroSinistre(String numeroSinistre) { this.numeroSinistre = numeroSinistre; }
    public LocalDate getDate() { return date; } public void setDate(LocalDate date) { this.date = date; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public BigDecimal getMontantEstime() { return montantEstime; } public void setMontantEstime(BigDecimal montantEstime) { this.montantEstime = montantEstime; }
    public String getTypeSinistre() { return typeSinistre; } public void setTypeSinistre(String typeSinistre) { this.typeSinistre = typeSinistre; }
    public String getPiecesJustificatives() { return piecesJustificatives; } public void setPiecesJustificatives(String piecesJustificatives) { this.piecesJustificatives = piecesJustificatives; }
    public StatutSinistre getStatut() { return statut; } public void setStatut(StatutSinistre statut) { this.statut = statut; }
    public String getCommentaires() { return commentaires; } public void setCommentaires(String commentaires) { this.commentaires = commentaires; }
    public BigDecimal getMontantIndemnisation() { return montantIndemnisation; } public void setMontantIndemnisation(BigDecimal montantIndemnisation) { this.montantIndemnisation = montantIndemnisation; }
    public Contrat getContrat() { return contrat; } public void setContrat(Contrat contrat) { this.contrat = contrat; }
    public List<PieceJustificative> getPieces() { return pieces; } public void setPieces(List<PieceJustificative> pieces) { this.pieces = pieces; }
    public List<Commentaire> getCommentairesEntities() { return commentairesEntities; } public void setCommentairesEntities(List<Commentaire> commentairesEntities) { this.commentairesEntities = commentairesEntities; }
}