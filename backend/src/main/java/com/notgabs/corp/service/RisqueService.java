package com.notgabs.corp.service;

import com.notgabs.corp.exception.BusinessException;
import com.notgabs.corp.exception.NotFoundException;
import com.notgabs.corp.model.Incident;
import com.notgabs.corp.model.PlanAction;
import com.notgabs.corp.model.Risque;
import com.notgabs.corp.interceptor.ValidateBusinessRules;
import com.notgabs.corp.interceptor.LogExecution;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@LogExecution
public class RisqueService {

    @Inject
    NotificationService notificationService;

    public List<Risque> listAll() {
        return Risque.listAll();
    }

    public Risque getById(UUID id) {
        Risque risque = Risque.findById(id);
        if (risque == null) {
            throw new NotFoundException("Risque not found");
        }
        return risque;
    }

    @Transactional
    @ValidateBusinessRules
    public Risque create(Risque risque) {
        if (risque.id != null) {
            throw new BusinessException("Id must not be set for a new risque.");
        }
        if (risque.proprietaire == null || risque.proprietaire.id == null) {
            throw new BusinessException("La définition d'un propriétaire est obligatoire lors de la création d'un risque.");
        }
        risque.persist();
        notificationService.notifsNouveauRisqueTresEleve(risque);
        return risque;
    }

    @Transactional
    @ValidateBusinessRules
    public Risque update(UUID id, Risque updateData) {
        Risque entity = getById(id);
        
        if (entity.statut == com.notgabs.corp.model.StatutRisque.CLOTURE) {
            throw new BusinessException("Modification interdite : Impossible de modifier un risque clôturé.");
        }
        if (updateData.proprietaire == null || updateData.proprietaire.id == null) {
            throw new BusinessException("La définition d'un propriétaire est obligatoire.");
        }
        
        entity.libelle = updateData.libelle;
        entity.description = updateData.description;
        entity.probabilite = updateData.probabilite;
        entity.gravite = updateData.gravite;
        entity.statut = updateData.statut;
        entity.proprietaire = updateData.proprietaire;
        
        notificationService.notifsNouveauRisqueTresEleve(entity);
        return entity;
    }

    @Transactional
    public void delete(UUID id) {
        Risque entity = getById(id);
        
        long incidentCount = Incident.count("risque", entity);
        long planCount = PlanAction.count("risque", entity);
        
        if (incidentCount > 0 || planCount > 0) {
            throw new BusinessException("Suppression interdite : Ce risque est lié à un Incident ou un Plan d'Action.");
        }
        
        entity.delete();
    }
}
