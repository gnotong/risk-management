package com.notgabs.corp.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Utilisateur extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(unique = true, nullable = false)
    public String username;

    public String nom;
    public String prenom;
    public String email;

    @Enumerated(EnumType.STRING)
    public Role role;

    public LocalDateTime lastLogin;

    @Column(name = "is_active")
    public boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "keycloak_sync_status", length = 50)
    public KeycloakSyncStatus keycloakSyncStatus = KeycloakSyncStatus.SYNCED;

    public static Utilisateur findByUsername(String username) {
        return find("username", username).firstResult();
    }
    
    public static java.util.List<Utilisateur> findPendingSync() {
        return find("keycloakSyncStatus != ?1", KeycloakSyncStatus.SYNCED).list();
    }
}
