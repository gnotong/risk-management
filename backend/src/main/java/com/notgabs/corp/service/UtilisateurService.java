package com.notgabs.corp.service;

import com.notgabs.corp.dto.CreateUserRequest;
import com.notgabs.corp.exception.BusinessException;
import com.notgabs.corp.exception.NotFoundException;
import com.notgabs.corp.model.PlanAction;
import com.notgabs.corp.model.Risque;
import com.notgabs.corp.model.Utilisateur;
import com.notgabs.corp.interceptor.ValidateBusinessRules;
import com.notgabs.corp.interceptor.LogExecution;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@LogExecution
public class UtilisateurService {

    @Inject
    KeycloakAdminService keycloakAdminService;

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

    /**
     * Create user with password and sync to Keycloak
     */
    @Transactional
    public Utilisateur createWithKeycloak(CreateUserRequest request) {
        // Validate input
        if (request.username == null || request.username.trim().isEmpty()) {
            throw new BusinessException("Username is required");
        }
        if (request.password == null || request.password.trim().isEmpty()) {
            throw new BusinessException("Password is required");
        }
        if (request.email == null || request.email.trim().isEmpty()) {
            throw new BusinessException("Email is required");
        }
        if (request.firstName == null || request.firstName.trim().isEmpty()) {
            throw new BusinessException("First name is required");
        }
        if (request.lastName == null || request.lastName.trim().isEmpty()) {
            throw new BusinessException("Last name is required");
        }
        if (request.role == null) {
            throw new BusinessException("Role is required");
        }

        // Check if user already exists
        Utilisateur existing = Utilisateur.findByUsername(request.username);
        if (existing != null) {
            throw new BusinessException("User with username '" + request.username + "' already exists");
        }

        try {
            // Create user in Keycloak first
            String keycloakUserId = keycloakAdminService.createUser(
                    request.username,
                    request.password,
                    request.firstName,
                    request.lastName,
                    request.email,
                    request.role.toString()
            );

            // Create user in database
            Utilisateur user = new Utilisateur();
            user.username = request.username;
            user.nom = request.lastName;
            user.prenom = request.firstName;
            user.email = request.email;
            user.role = request.role;
            user.isActive = true;

            user.persist();
            
            Log.info("User " + request.username + " created successfully in both Keycloak and database");
            return user;
        } catch (Exception e) {
            Log.error("Error creating user with Keycloak", e);
            throw new BusinessException("Failed to create user: " + e.getMessage());
        }
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
