package com.notgabs.corp.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Incident extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(nullable = false)
    public String titre;

    @Column(length = 2000, nullable = false)
    public String description;

    @Column(name = "date_occurence")
    public LocalDateTime dateOccurence;

    @Transient
    public String commentaireUpdate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public SeveriteIncident severite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public StatutIncident statut;

    @Column(name = "taux_avancement", nullable = false)
    public int tauxAvancement = 0;

    @ManyToOne
    @JoinColumn(name = "risque_id")
    public Risque risque;

    @ManyToOne
    @JoinColumn(name = "signaleur_id")
    public Utilisateur signaleur;

    @Column(name = "impact_reel")
    public String impactReel; // Financier, Image, Operationnel

    @Column(name = "date_creation", nullable = false, updatable = false)
    public LocalDateTime dateCreation;

    @Column(name = "date_resolution")
    public LocalDateTime dateResolution;

    @PrePersist
    public void prePersist() {
        if (this.statut == null) {
            this.statut = StatutIncident.OUVERT;
        }
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (this.statut == StatutIncident.RESOLU || this.statut == StatutIncident.CLOTURE) {
            if (this.dateResolution == null) {
                this.dateResolution = LocalDateTime.now();
            }
        } else {
            this.dateResolution = null;
        }
    }
}
