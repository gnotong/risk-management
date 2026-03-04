package com.notgabs.corp.service;

import com.notgabs.corp.exception.BusinessException;
import com.notgabs.corp.exception.NotFoundException;
import com.notgabs.corp.model.Incident;
import com.notgabs.corp.model.StatutIncident;
import com.notgabs.corp.model.SeveriteIncident;
import com.notgabs.corp.interceptor.ValidateBusinessRules;
import com.notgabs.corp.interceptor.LogExecution;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.notgabs.corp.model.SuiviIncident;

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
        
        // Ensure starting state
        if (incident.tauxAvancement == 0) {
            incident.statut = StatutIncident.OUVERT;
        } else if (incident.tauxAvancement == 100) {
            incident.statut = StatutIncident.RESOLU;
            incident.dateResolution = LocalDateTime.now();
        } else {
            incident.statut = StatutIncident.EN_COURS;
        }

        incident.persist();
        return incident;
    }
    @Transactional
    @ValidateBusinessRules
    public Incident update(UUID id, Incident updatedIncident) {
        Incident existing = getById(id);
        
        // Business Rules Validations
        if (updatedIncident.tauxAvancement == 100) {
            if (updatedIncident.severite == SeveriteIncident.ELEVE || updatedIncident.severite == SeveriteIncident.CRITIQUE) {
                if (updatedIncident.impactReel == null || updatedIncident.impactReel.trim().isEmpty()) {
                    throw new BusinessException("Impossible de résoudre un incident majeur sans préciser l'impact réel.");
                }
                if (updatedIncident.risque == null) {
                    throw new BusinessException("Impossible de résoudre un incident majeur sans le lier à un risque de la cartographie.");
                }
            }
        }
        
        // Status Auto-Sync based on Taux d'Avancement, unless manually CLOSED
        if (updatedIncident.statut == StatutIncident.CLOTURE) {
            updatedIncident.tauxAvancement = 100;
            if (existing.dateResolution == null) {
                updatedIncident.dateResolution = LocalDateTime.now();
            } else {
                updatedIncident.dateResolution = existing.dateResolution;
            }
        } else {
            if (updatedIncident.tauxAvancement == 0) {
                updatedIncident.statut = StatutIncident.OUVERT;
                updatedIncident.dateResolution = null;
            } else if (updatedIncident.tauxAvancement > 0 && updatedIncident.tauxAvancement < 100) {
                updatedIncident.statut = StatutIncident.EN_COURS;
                updatedIncident.dateResolution = null; 
            } else if (updatedIncident.tauxAvancement == 100) {
                updatedIncident.statut = StatutIncident.RESOLU;
                if (existing.dateResolution == null) {
                    updatedIncident.dateResolution = LocalDateTime.now();
                } else {
                    updatedIncident.dateResolution = existing.dateResolution;
                }
            }
        }

        StringBuilder changes = new StringBuilder();
        if (existing.statut != updatedIncident.statut) {
            changes.append("Statut passé à ").append(updatedIncident.statut).append(". ");
        }
        if (existing.severite != updatedIncident.severite) {
            changes.append("Sévérité passée à ").append(updatedIncident.severite).append(". ");
        }
        if (existing.tauxAvancement != updatedIncident.tauxAvancement) {
            changes.append("Avancement de ").append(existing.tauxAvancement).append("% à ").append(updatedIncident.tauxAvancement).append("%. ");
        }
        
        existing.titre = updatedIncident.titre;
        existing.description = updatedIncident.description;
        existing.dateOccurence = updatedIncident.dateOccurence;
        existing.severite = updatedIncident.severite;
        existing.statut = updatedIncident.statut;
        existing.tauxAvancement = updatedIncident.tauxAvancement;
        existing.risque = updatedIncident.risque;
        existing.signaleur = updatedIncident.signaleur;
        existing.impactReel = updatedIncident.impactReel;
        existing.dateResolution = updatedIncident.dateResolution;
        
        String finalSuivi = "";
        if (updatedIncident.commentaireUpdate != null && !updatedIncident.commentaireUpdate.trim().isEmpty()) {
            finalSuivi = updatedIncident.commentaireUpdate.trim();
            if (changes.length() > 0) {
                finalSuivi += " (" + changes.toString().trim() + ")";
            }
        } else if (changes.length() > 0) {
            finalSuivi = "Mise à jour: " + changes.toString().trim();
        }

        if (!finalSuivi.isEmpty()) {
            SuiviIncident suivi = new SuiviIncident();
            suivi.incident = existing;
            suivi.commentaire = finalSuivi;
            suivi.dateSuivi = LocalDateTime.now();
            suivi.persist();
        }
        
        return existing;
    }

    public List<SuiviIncident> getSuivis(UUID incidentId) {
        Incident incident = getById(incidentId);
        return SuiviIncident.find("incident = ?1 order by dateSuivi desc", incident).list();
    }

    @Transactional
    public SuiviIncident addSuivi(UUID incidentId, SuiviIncident suivi) {
        Incident incident = getById(incidentId);
        suivi.incident = incident;
        suivi.dateSuivi = LocalDateTime.now();
        suivi.persist();
        return suivi;
    }

    @Transactional
    public void deleteSuivi(UUID incidentId, UUID suiviId) {
        getById(incidentId); // verify existence and access
        SuiviIncident suivi = SuiviIncident.findById(suiviId);
        if (suivi == null || !suivi.incident.id.equals(incidentId)) {
            throw new NotFoundException("Suivi incident not found");
        }
        suivi.delete();
    }

    @Transactional
    @LogExecution
    public void delete(UUID id) {
        Incident existing = getById(id);
        SuiviIncident.delete("incident.id = ?1", existing.id);
        existing.delete();
    }
}
