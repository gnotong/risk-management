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

    @Column(length = 2000, nullable = false)
    public String description;

    @Column(name = "date_occurence")
    public LocalDateTime dateOccurence;

    public int gravite;

    @ManyToOne
    @JoinColumn(name = "risque_id")
    public Risque risque;

    @Column(name = "impact_reel")
    public String impactReel; // Financier, Image, Operationnel
}
