package com.notgabs.corp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.fasterxml.jackson.databind.JsonNode;

@ApplicationScoped
public class KeycloakAdminService {

    @ConfigProperty(name = "keycloak.admin.username", defaultValue = "admin")
    String adminUsername;

    @ConfigProperty(name = "keycloak.admin.password", defaultValue = "admin")
    String adminPassword;

    @ConfigProperty(name = "keycloak.server-url")
    String keycloakServerUrl;

    @ConfigProperty(name = "keycloak.realm", defaultValue = "risk-realm")
    String realm;

    private String adminToken;
    private long tokenExpiry;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Get admin access token from Keycloak
     */
    private synchronized String getAdminToken() {
        if (adminToken != null && System.currentTimeMillis() < tokenExpiry) {
            return adminToken;
        }

        try {
            String tokenUrl = keycloakServerUrl + "/realms/master/protocol/openid-connect/token";

            String bodyContent = "grant_type=password&client_id=admin-cli&username=" + adminUsername 
                    + "&password=" + adminPassword;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(tokenUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(bodyContent))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode jsonNode = objectMapper.readTree(response.body());
                adminToken = jsonNode.get("access_token").asText();
                tokenExpiry = System.currentTimeMillis() + (jsonNode.get("expires_in").asLong() * 1000);
                Log.info("Keycloak admin token obtained successfully");
                return adminToken;
            } else {
                Log.error("Failed to get Keycloak admin token: " + response.body());
                throw new RuntimeException("Failed to authenticate with Keycloak");
            }
        } catch (Exception e) {
            Log.error("Error obtaining Keycloak admin token", e);
            throw new RuntimeException("Failed to get Keycloak admin token", e);
        }
    }

    /**
     * Create a user in Keycloak
     */
    public String createUser(String username, String password, String firstName, String lastName, String email, String role) {
        try {
            String token = getAdminToken();
            String createUserUrl = keycloakServerUrl + "/admin/realms/" + realm + "/users";

            String userJson = String.format(
                    "{\"username\":\"%s\",\"email\":\"%s\",\"firstName\":\"%s\",\"lastName\":\"%s\",\"enabled\":true}",
                    username, email, firstName, lastName);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(createUserUrl))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(userJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                String location = response.headers().firstValue("Location").orElse("");
                String userId = location.substring(location.lastIndexOf("/") + 1);
                Log.info("User " + username + " created in Keycloak with ID: " + userId);

                // Set temporary password
                setUserPassword(userId, password, token);

                // Assign role
                assignRoleToUser(userId, role, token);

                return userId;
            } else if (response.statusCode() == 409) {
                Log.warn("User " + username + " already exists in Keycloak");
                // Return existing user ID
                return getUserId(username, token);
            } else {
                Log.error("Failed to create user in Keycloak: " + response.body());
                throw new RuntimeException("Failed to create user in Keycloak: " + response.body());
            }
        } catch (Exception e) {
            Log.error("Error creating user in Keycloak", e);
            throw new RuntimeException("Failed to create user in Keycloak", e);
        }
    }

    /**
     * Set user password in Keycloak
     */
    private void setUserPassword(String userId, String password, String token) {
        try {
            String passwordUrl = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + userId + "/reset-password";

            String passwordJson = String.format(
                    "{\"type\":\"password\",\"value\":\"%s\",\"temporary\":false}",
                    password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(passwordUrl))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(passwordJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 204) {
                Log.info("Password set for user " + userId);
            } else {
                Log.error("Failed to set password for user: " + response.body());
            }
        } catch (Exception e) {
            Log.error("Error setting user password", e);
        }
    }

    /**
     * Assign role to user
     */
    private void assignRoleToUser(String userId, String roleName, String token) {
        try {
            // Get role ID from role name
            String rolesUrl = keycloakServerUrl + "/admin/realms/" + realm + "/roles/" + roleName;
            HttpRequest getRoleRequest = HttpRequest.newBuilder()
                    .uri(URI.create(rolesUrl))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> roleResponse = httpClient.send(getRoleRequest, HttpResponse.BodyHandlers.ofString());

            if (roleResponse.statusCode() == 200) {
                JsonNode roleNode = objectMapper.readTree(roleResponse.body());
                String roleId = roleNode.get("id").asText();

                // Assign role to user
                String assignUrl = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";
                String roleJson = String.format("[{\"id\":\"%s\",\"name\":\"%s\"}]", roleId, roleName);

                HttpRequest assignRequest = HttpRequest.newBuilder()
                        .uri(URI.create(assignUrl))
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(roleJson))
                        .build();

                HttpResponse<String> assignResponse = httpClient.send(assignRequest, HttpResponse.BodyHandlers.ofString());

                if (assignResponse.statusCode() == 204) {
                    Log.info("Role " + roleName + " assigned to user " + userId);
                } else {
                    Log.warn("Failed to assign role to user: " + assignResponse.body());
                }
            }
        } catch (Exception e) {
            Log.error("Error assigning role to user", e);
        }
    }

    /**
     * Get user ID by username
     */
    private String getUserId(String username, String token) {
        try {
            String searchUrl = keycloakServerUrl + "/admin/realms/" + realm + "/users?username=" + username;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(searchUrl))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode users = objectMapper.readTree(response.body());
                if (users.isArray() && users.size() > 0) {
                    return users.get(0).get("id").asText();
                }
            }
        } catch (Exception e) {
            Log.error("Error getting user ID", e);
        }
        return null;
    }
}
