package com.notgabs.corp.service;

import com.notgabs.corp.exception.BusinessException;
import com.notgabs.corp.exception.NotFoundException;
import com.notgabs.corp.model.PlanAction;
import com.notgabs.corp.model.Risque;
import com.notgabs.corp.model.Utilisateur;
import com.notgabs.corp.interceptor.ValidateBusinessRules;
import com.notgabs.corp.interceptor.LogExecution;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@LogExecution
public class UtilisateurService {

    public List<Utilisateur> listAll() {
        return Utilisateur.listAll();
    }

    public Utilisateur getById(UUID id) {
        Utilisateur user = Utilisateur.findById(id);
        if (user == null) {
            throw new NotFoundException("Utilisateur not found");
        }
        return user;
    }

    @Transactional
    @ValidateBusinessRules
    public Utilisateur create(Utilisateur user) {
        if (user.id != null) {
            throw new BusinessException("Id must not be set for a new utilisateur.");
        }
        user.persist();
        return user;
    }

    @Transactional
    public void delete(UUID id) {
        Utilisateur entity = getById(id);
        
        long riskCount = Risque.count("proprietaire", entity);
        long planCount = PlanAction.count("responsable", entity);
        
        if (riskCount > 0 || planCount > 0) {
            throw new BusinessException("Suppression interdite : Cet utilisateur est responsable d'un Risque ou d'un Plan d'Action.");
        }
        
        entity.delete();
    }
}
