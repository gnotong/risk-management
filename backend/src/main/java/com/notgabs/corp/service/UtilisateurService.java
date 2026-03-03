package com.notgabs.corp.service;

import com.notgabs.corp.dto.CreateUserRequest;
import com.notgabs.corp.dto.UpdateUserRequest;
import com.notgabs.corp.exception.BusinessException;
import com.notgabs.corp.exception.NotFoundException;
import com.notgabs.corp.model.KeycloakSyncStatus;
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

            // Create user in database first
            Utilisateur user = new Utilisateur();
            user.username = request.username;
            user.nom = request.lastName;
            user.prenom = request.firstName;
            user.email = request.email;
            user.role = request.role;
            user.isActive = true;

        try {
            // Create user in Keycloak
            String keycloakUserId = keycloakAdminService.createUser(
                    request.username,
                    request.password,
                    request.firstName,
                    request.lastName,
                    request.email,
                    request.role.toString()
            );
            user.keycloakSyncStatus = KeycloakSyncStatus.SYNCED;
            Log.info("User " + request.username + " created successfully in both Keycloak and database");
        } catch (Exception e) {
            Log.warn("Error creating user with Keycloak, marking as PENDING_CREATE", e);
            user.keycloakSyncStatus = KeycloakSyncStatus.PENDING_CREATE;
        }
        
        user.persist();
        return user;
    }

    @Transactional
    public Utilisateur updateWithKeycloak(UUID id, UpdateUserRequest request) {
        Utilisateur existing = getById(id);

        if (request.username == null || request.username.trim().isEmpty()) {
            throw new BusinessException("Username is required");
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

        if (!existing.username.equals(request.username)) {
            Utilisateur usernameCheck = Utilisateur.findByUsername(request.username);
            if (usernameCheck != null) {
                throw new BusinessException("User with username '" + request.username + "' already exists");
            }
        }

        existing.username = request.username;
        existing.nom = request.lastName;
        existing.prenom = request.firstName;
        existing.email = request.email;
        existing.role = request.role;
        if (request.isActive != null) {
            existing.isActive = request.isActive;
        }

        try {
            String keycloakUserId = keycloakAdminService.getKeycloakUserId(existing.username);
            
            if (keycloakUserId == null) {
                // Si l'utilisateur n'existe pas dans Keycloak (ex: échec création précédente)
                // On peut soit essayer de le créer, soit logguer un avertissement.
                // Par souci de simplicité pour le scheduler, on déclenche une création.
                keycloakAdminService.createUser(
                    existing.username,
                    request.password != null ? request.password : UUID.randomUUID().toString(),
                    existing.prenom,
                    existing.nom,
                    existing.email,
                    existing.role.toString()
                );
            } else {
                boolean isEnabled = request.isActive != null ? request.isActive : existing.isActive;
                keycloakAdminService.updateUser(
                        keycloakUserId,
                        request.username,
                        request.firstName,
                        request.lastName,
                        request.email,
                        request.role.toString(),
                        request.password,
                        isEnabled
                );
            }
            existing.keycloakSyncStatus = KeycloakSyncStatus.SYNCED;
            Log.info("User " + existing.username + " updated successfully in Keycloak");
        } catch (Exception e) {
            Log.warn("Error updating user with Keycloak, marking as PENDING_UPDATE", e);
            if (existing.keycloakSyncStatus != KeycloakSyncStatus.PENDING_CREATE) {
                existing.keycloakSyncStatus = KeycloakSyncStatus.PENDING_UPDATE;
            }
        }
        
        return existing;
    }

    @Transactional
    public void delete(UUID id) {
        Utilisateur entity = getById(id);
        
        long riskCount = Risque.count("proprietaire", entity);
        long planCount = PlanAction.count("responsable", entity);
        
        if (riskCount > 0 || planCount > 0) {
            throw new BusinessException("Suppression interdite : Cet utilisateur est responsable d'un Risque ou d'un Plan d'Action.");
        }
        
        try {
            String keycloakUserId = keycloakAdminService.getKeycloakUserId(entity.username);
            if (keycloakUserId != null) {
                keycloakAdminService.deleteUser(keycloakUserId);
                Log.info("User " + entity.username + " deleted from Keycloak");
            } else {
                Log.warn("User " + entity.username + " not found in Keycloak during deletion");
            }
            entity.delete(); // Delete locally if Keycloak sync was successful or user not found
        } catch (Exception e) {
            Log.warn("Failed to delete user from Keycloak, marking as PENDING_DELETE", e);
            entity.keycloakSyncStatus = KeycloakSyncStatus.PENDING_DELETE;
            entity.isActive = false; // Disable so it doesn't show up in normal lists
        }
    }
    
    @Transactional
    public void syncUserWithKeycloak(UUID id) {
        Utilisateur user = getById(id);
        
        if (user.keycloakSyncStatus == KeycloakSyncStatus.SYNCED) {
            return;
        }
        
        try {
            if (user.keycloakSyncStatus == KeycloakSyncStatus.PENDING_CREATE) {
                String keycloakUserId = keycloakAdminService.getKeycloakUserId(user.username);
                if (keycloakUserId == null) {
                    keycloakAdminService.createUser(
                        user.username,
                        UUID.randomUUID().toString(), // Admin will have to reset it or user uses 'forgot password'
                        user.prenom,
                        user.nom,
                        user.email,
                        user.role.toString()
                    );
                }
                user.keycloakSyncStatus = KeycloakSyncStatus.SYNCED;
            } else if (user.keycloakSyncStatus == KeycloakSyncStatus.PENDING_UPDATE) {
                String keycloakUserId = keycloakAdminService.getKeycloakUserId(user.username);
                if (keycloakUserId != null) {
                    keycloakAdminService.updateUser(
                        keycloakUserId,
                        user.username,
                        user.prenom,
                        user.nom,
                        user.email,
                        user.role.toString(),
                        null,
                        user.isActive
                    );
                }
                user.keycloakSyncStatus = KeycloakSyncStatus.SYNCED;
            } else if (user.keycloakSyncStatus == KeycloakSyncStatus.PENDING_DELETE) {
                String keycloakUserId = keycloakAdminService.getKeycloakUserId(user.username);
                if (keycloakUserId != null) {
                    keycloakAdminService.deleteUser(keycloakUserId);
                }
                user.delete();
            }
        } catch (Exception e) {
            Log.error("Failed to sync user with Keycloak during manual sync: " + user.username, e);
            throw new BusinessException("Failed to synchronize with Keycloak: " + e.getMessage());
        }
    }
}
