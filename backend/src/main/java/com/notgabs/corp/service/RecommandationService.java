package com.notgabs.corp.service;

import com.notgabs.corp.exception.BusinessException;
import com.notgabs.corp.exception.NotFoundException;
import com.notgabs.corp.model.Recommandation;
import com.notgabs.corp.interceptor.ValidateBusinessRules;
import com.notgabs.corp.interceptor.LogExecution;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@LogExecution
public class RecommandationService {

    public List<Recommandation> listAll() {
        return Recommandation.listAll();
    }

    public Recommandation getById(UUID id) {
        Recommandation entity = Recommandation.findById(id);
        if (entity == null) {
            throw new NotFoundException("Recommandation not found");
        }
        return entity;
    }

    @Transactional
    @ValidateBusinessRules
    public Recommandation create(Recommandation recommandation) {
        if (recommandation.id != null) {
            throw new BusinessException("Id must not be set for a new recommandation");
        }
        recommandation.persist();
        return recommandation;
    }
}
