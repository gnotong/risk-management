package com.notgabs.corp.service;

import com.notgabs.corp.model.Utilisateur;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class KeycloakSyncScheduler {

    @Inject
    UtilisateurService utilisateurService;

    @Scheduled(every = "5m")
    @Transactional
    public void syncPendingUsers() {
        List<Utilisateur> pendingUsers = Utilisateur.findPendingSync();
        
        if (pendingUsers.isEmpty()) {
            return;
        }

        Log.info("Found " + pendingUsers.size() + " users pending Keycloak synchronization. Starting sync...");

        for (Utilisateur user : pendingUsers) {
            try {
                utilisateurService.syncUserWithKeycloak(user.id);
                Log.info("Successfully synced user: " + user.username);
            } catch (Exception e) {
                Log.error("Failed to sync user: " + user.username + " during scheduled run", e);
            }
        }
    }
}
