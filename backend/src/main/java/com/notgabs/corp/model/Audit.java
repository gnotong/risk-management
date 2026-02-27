package com.notgabs.corp.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Audit extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(nullable = false)
    public String nom;

    @Column(length = 2000)
    public String description;

    @Column(name = "date_realisation")
    public LocalDate dateRealisation;

    @ManyToOne
    @JoinColumn(name = "auditeur_id")
    public Utilisateur auditeur;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_audit")
    public StatutAudit statutAudit;
}
