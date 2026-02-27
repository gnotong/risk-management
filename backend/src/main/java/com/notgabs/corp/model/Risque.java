package com.notgabs.corp.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Risque extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(nullable = false)
    public String libelle;

    @Column(length = 2000)
    public String description;

    public int probabilite; // 1-3
    public int gravite; // 1-3
    public int score; // Calculated: P * G

    @Enumerated(EnumType.STRING)
    public StatutRisque statut;

    @ManyToOne
    @JoinColumn(name = "proprietaire_id")
    public Utilisateur proprietaire;

    @Column(name = "date_creation")
    public LocalDateTime dateCreation;

    @PrePersist
    @PreUpdate
    public void calculerScoreEtDate() {
        this.score = this.probabilite * this.gravite;
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
    }
}
