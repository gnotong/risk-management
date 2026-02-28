package com.notgabs.corp.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class PlanAction extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(nullable = false)
    public String nom;

    @Column(length = 2000)
    public String description;

    @Column(name = "date_debut")
    public LocalDate dateDebut;

    @Column(name = "date_fin")
    public LocalDate dateFin;

    @ManyToOne
    @JoinColumn(name = "responsable_id")
    public Utilisateur responsable;

    @ManyToOne
    @JoinColumn(name = "risque_id")
    public Risque risque;

    @Column(name = "taux_avancement")
    public int tauxAvancement; // 0-100%

    @Enumerated(EnumType.STRING)
    public StatutPlanAction statut;
}
