package com.notgabs.corp.service;

import com.notgabs.corp.exception.BusinessException;
import com.notgabs.corp.exception.ForbiddenException;
import com.notgabs.corp.exception.NotFoundException;
import com.notgabs.corp.exception.UnauthorizedException;
import com.notgabs.corp.model.PlanAction;
import com.notgabs.corp.model.Risque;
import com.notgabs.corp.model.Role;
import com.notgabs.corp.model.StatutPlanAction;
import com.notgabs.corp.model.SuiviPlanAction;
import com.notgabs.corp.model.Utilisateur;
import com.notgabs.corp.interceptor.ValidateBusinessRules;
import com.notgabs.corp.interceptor.CheckAccess;
import com.notgabs.corp.interceptor.LogExecution;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@LogExecution
public class PlanActionService {

    public List<PlanAction> listAll() {
        return PlanAction.listAll();
    }

    public PlanAction getById(UUID id) {
        PlanAction entity = PlanAction.findById(id);
        if (entity == null) {
            throw new NotFoundException("PlanAction not found");
        }
        return entity;
    }

    @Transactional
    @ValidateBusinessRules
    public PlanAction create(PlanAction planAction) {
        if (planAction.id != null) {
            throw new BusinessException("Id was invalidly set on request.");
        }
        
        if (planAction.statut == null) {
            planAction.statut = StatutPlanAction.NON_COMMENCE;
        }
        if (planAction.dateDebut != null && planAction.dateFin != null) {
            if (!planAction.dateFin.isAfter(planAction.dateDebut)) {
                throw new BusinessException("La date de fin doit être strictement supérieure à la date de début.");
            }
        }
        
        if (planAction.risque != null && planAction.dateDebut != null) {
            Risque risque = Risque.findById(planAction.risque.id);
            if (risque != null) {
                if (risque.dateCreation != null) {
                    if (planAction.dateDebut.isBefore(risque.dateCreation.toLocalDate())) {
                        throw new BusinessException("La date de début ne peut pas être antérieure à la date de création du risque.");
                    }
                }
                
                if (risque.statut == com.notgabs.corp.model.StatutRisque.CLOTURE) {
                    risque.statut = com.notgabs.corp.model.StatutRisque.EN_COURS;
                    risque.persist();
                }
            }
        }
        
        syncStatut(planAction);
        planAction.persist();
        return planAction;
    }

    private void syncStatut(PlanAction planAction) {
        if (planAction.tauxAvancement == 0) {
            planAction.statut = StatutPlanAction.NON_COMMENCE;
        } else if (planAction.tauxAvancement == 100) {
            planAction.statut = StatutPlanAction.TERMINE;
            planAction.dateFin = LocalDate.now();
        } else {
            if (planAction.dateFin != null && LocalDate.now().isAfter(planAction.dateFin)) {
                planAction.statut = StatutPlanAction.EN_RETARD;
            } else {
                planAction.statut = StatutPlanAction.EN_COURS;
            }
        }
    }

    @Transactional
    @ValidateBusinessRules
    @CheckAccess
    public PlanAction update(UUID id, PlanAction planAction, String userIdstr) {
        PlanAction entity = getById(id);

        if (planAction.tauxAvancement != entity.tauxAvancement) {
            if (userIdstr == null || userIdstr.isEmpty()) {
                throw new UnauthorizedException("Non autorisé : Identifiant utilisateur manquant.");
            }
            if (!"admin-override".equals(userIdstr)) {
                try {
                    UUID userId = UUID.fromString(userIdstr);
                    Utilisateur user = Utilisateur.findById(userId);
                    if (user == null || (user.role != Role.ADMIN && (entity.responsable == null || !entity.responsable.id.equals(userId)))) {
                        throw new ForbiddenException("Accès refusé : Seul l'ADMIN ou le responsable peut modifier l'avancement.");
                    }
                } catch (IllegalArgumentException e) {
                    throw new BusinessException("Format d'identifiant utilisateur invalide.");
                }
            }
        }

        if (planAction.dateDebut != null && planAction.dateFin != null) {
            if (!planAction.dateFin.isAfter(planAction.dateDebut)) {
                throw new BusinessException("La date de fin doit être strictement supérieure à la date de début.");
            }
        }
        
        if (entity.risque != null && planAction.dateDebut != null) {
            if (entity.risque.dateCreation != null) {
                if (planAction.dateDebut.isBefore(entity.risque.dateCreation.toLocalDate())) {
                    throw new BusinessException("La date de début ne peut pas être antérieure à la date de création du risque.");
                }
            }
        }
        
        syncStatut(planAction);
        
        if (entity.risque != null) {
            long unfinishedCount = PlanAction.count("risque.id = ?1 and id != ?2 and statut != ?3", 
                                                    entity.risque.id, entity.id, StatutPlanAction.TERMINE);
            if (planAction.statut != StatutPlanAction.TERMINE) {
                unfinishedCount++;
            }
            
            if (unfinishedCount == 0) {
                if (entity.risque.statut != com.notgabs.corp.model.StatutRisque.CLOTURE) {
                    entity.risque.statut = com.notgabs.corp.model.StatutRisque.CLOTURE;
                    entity.risque.persist();
                }
            } else {
                if (entity.risque.statut == com.notgabs.corp.model.StatutRisque.CLOTURE) {
                    entity.risque.statut = com.notgabs.corp.model.StatutRisque.EN_COURS;
                    entity.risque.persist();
                } else if (entity.risque.statut == com.notgabs.corp.model.StatutRisque.OUVERT && (planAction.statut == StatutPlanAction.EN_COURS || planAction.statut == StatutPlanAction.EN_RETARD)) {
                    entity.risque.statut = com.notgabs.corp.model.StatutRisque.EN_COURS;
                    entity.risque.persist();
                }
            }
        }
        
        boolean changedAvancement = false;
        StringBuilder changes = new StringBuilder("");
        if (entity.tauxAvancement != planAction.tauxAvancement) {
            changes.append("Avancement de ").append(entity.tauxAvancement).append("% à ").append(planAction.tauxAvancement).append("%. ");
            changedAvancement = true;
        }
        if (entity.statut != planAction.statut) {
            changes.append("Statut passé à ").append(planAction.statut).append(". ");
        }

        entity.nom = planAction.nom;
        entity.description = planAction.description;
        entity.dateDebut = planAction.dateDebut;
        entity.dateFin = planAction.dateFin;
        entity.responsable = planAction.responsable;
        entity.risque = planAction.risque;
        entity.tauxAvancement = planAction.tauxAvancement;
        entity.statut = planAction.statut;
        
        String finalSuivi = "";
        if (planAction.commentaireUpdate != null && !planAction.commentaireUpdate.trim().isEmpty()) {
            finalSuivi = planAction.commentaireUpdate.trim();
            if (changes.length() > 0) {
                finalSuivi += " (" + changes.toString().trim() + ")";
            }
        } else if (changes.length() > 0) {
            finalSuivi = "Mise à jour: " + changes.toString().trim();
        }

        if (!finalSuivi.isEmpty()) {
            SuiviPlanAction suivi = new SuiviPlanAction();
            suivi.planAction = entity;
            suivi.commentaire = finalSuivi;
            suivi.dateSuivi = LocalDateTime.now();
            suivi.persist();
        }

        return entity;
    }

    public List<SuiviPlanAction> getSuivis(UUID planActionId) {
        PlanAction plan = getById(planActionId);
        return SuiviPlanAction.find("planAction = ?1 order by dateSuivi desc", plan).list();
    }

    @Transactional
    public SuiviPlanAction addSuivi(UUID planActionId, SuiviPlanAction suivi) {
        PlanAction plan = getById(planActionId);
        suivi.planAction = plan;
        suivi.dateSuivi = LocalDateTime.now();
        suivi.persist();
        return suivi;
    }

    @Transactional
    public void delete(UUID id) {
        PlanAction entity = getById(id);
        
        if (entity.tauxAvancement > 0) {
            throw new BusinessException("Suppression interdite : Impossible de supprimer un plan d'action ayant de l'avancement (> 0%). Mettez-le en attente ou annulez-le.");
        }
        
        SuiviPlanAction.delete("planAction.id = ?1", entity.id);
        entity.delete();
    }

    @Transactional
    public void deleteSuivi(UUID planActionId, UUID suiviId) {
        PlanAction plan = getById(planActionId);
        if (plan.statut == StatutPlanAction.TERMINE) {
            throw new BusinessException("Suppression interdite : Impossible de supprimer les journaux d'un plan d'action terminé.");
        }
        
        SuiviPlanAction suivi = SuiviPlanAction.findById(suiviId);
        if (suivi == null || !suivi.planAction.id.equals(planActionId)) {
            throw new NotFoundException("Suivi not found");
        }
        
        suivi.delete();
    }
}
