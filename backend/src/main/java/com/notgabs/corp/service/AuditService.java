package com.notgabs.corp.service;

import com.notgabs.corp.exception.BusinessException;
import com.notgabs.corp.exception.NotFoundException;
import com.notgabs.corp.model.Audit;
import com.notgabs.corp.model.Recommandation;
import com.notgabs.corp.model.StatutAudit;
import com.notgabs.corp.model.StatutRecommandation;
import com.notgabs.corp.interceptor.ValidateBusinessRules;
import com.notgabs.corp.interceptor.LogExecution;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@LogExecution
public class AuditService {

    public List<Audit> listAll() {
        return Audit.listAll();
    }

    public Audit getById(UUID id) {
        Audit audit = Audit.findById(id);
        if (audit == null) {
            throw new NotFoundException("Audit not found");
        }
        return audit;
    }

    @Transactional
    public Audit create(Audit audit) {
        if (audit.id != null) {
            throw new BusinessException("Id must not be set for a new audit");
        }
        audit.persist();
        return audit;
    }

    @Transactional
    @ValidateBusinessRules
    public Audit update(UUID id, Audit updateData) {
        Audit entity = getById(id);

        if (updateData.statutAudit == StatutAudit.TERMINE && entity.statutAudit != StatutAudit.TERMINE) {
            long openRecs = Recommandation.count("audit = ?1 and statut != ?2", entity, StatutRecommandation.TERMINE);
            if (openRecs > 0) {
                throw new BusinessException("Clôture interdite : L'audit a des recommandations non terminées.");
            }
        }

        entity.nom = updateData.nom;
        entity.description = updateData.description;
        entity.dateRealisation = updateData.dateRealisation;
        entity.auditeur = updateData.auditeur;
        entity.statutAudit = updateData.statutAudit;
        return entity;
    }

    @Transactional
    public void delete(UUID id) {
        Audit entity = getById(id);
        entity.delete();
    }
}
