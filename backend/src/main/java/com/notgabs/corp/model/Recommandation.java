package com.notgabs.corp.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Recommandation extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(length = 2000, nullable = false)
    public String description;

    @Enumerated(EnumType.STRING)
    public StatutRecommandation statut;

    @ManyToOne
    @JoinColumn(name = "audit_id")
    public Audit audit;

    @Enumerated(EnumType.STRING)
    public Priorite priorite;
}
