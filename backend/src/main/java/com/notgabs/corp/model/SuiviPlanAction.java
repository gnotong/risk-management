package com.notgabs.corp.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class SuiviPlanAction extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_action_id")
    public PlanAction planAction;

    @Column(nullable = false, length = 1000)
    public String commentaire;

    @Column(name = "date_suivi", nullable = false)
    public LocalDateTime dateSuivi;
}
