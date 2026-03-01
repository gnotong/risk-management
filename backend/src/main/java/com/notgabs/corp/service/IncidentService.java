package com.notgabs.corp.service;

import com.notgabs.corp.exception.BusinessException;
import com.notgabs.corp.exception.NotFoundException;
import com.notgabs.corp.model.Incident;
import com.notgabs.corp.interceptor.ValidateBusinessRules;
import com.notgabs.corp.interceptor.LogExecution;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@LogExecution
public class IncidentService {

    public List<Incident> listAll() {
        return Incident.listAll();
    }

    public Incident getById(UUID id) {
        Incident entity = Incident.findById(id);
        if (entity == null) {
            throw new NotFoundException("Incident not found");
        }
        return entity;
    }

    @Transactional
    @ValidateBusinessRules
    public Incident create(Incident incident) {
        if (incident.id != null) {
            throw new BusinessException("Id must not be set for a new incident");
        }
        incident.persist();
        return incident;
    }
}
