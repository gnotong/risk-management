package com.notgabs.corp.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

@Entity
public class PlanAction extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(nullable = false)
    @NotBlank(message = "Le nom du plan d'action ne peut pas être vide")
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
    @NotNull(message = "Le plan d'action doit être lié à un risque")
    public Risque risque;

    @Column(name = "taux_avancement")
    @Min(value = 0, message = "Le taux d'avancement ne peut pas être négatif")
    @Max(value = 100, message = "Le taux d'avancement ne peut pas dépasser 100")
    public int tauxAvancement; // 0-100%

    @Enumerated(EnumType.STRING)
    public StatutPlanAction statut;

    @OneToMany(mappedBy = "planAction", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    public List<SuiviPlanAction> suivis;

    @Transient
    public String commentaireUpdate;
}
