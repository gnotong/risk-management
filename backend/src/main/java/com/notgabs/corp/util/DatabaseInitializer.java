package com.notgabs.corp.util;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@ApplicationScoped
public class DatabaseInitializer {

    @Inject
    EntityManager em;

    @Transactional
    public void onStart(@Observes StartupEvent ev) {
        // Check if database is empty (no users)
        long count = (long) em.createQuery("SELECT COUNT(u) FROM Utilisateur u").getSingleResult();
        
        if (count == 0) {
            System.out.println("Base de données vide détectée. Exécution de import.sql...");
            try (InputStream is = getClass().getResourceAsStream("/import.sql")) {
                if (is != null) {
                    try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name())) {
                        scanner.useDelimiter(";");
                        while (scanner.hasNext()) {
                            String statement = scanner.next().trim();
                            
                            // Remove SQL comments from the statement
                            StringBuilder cleanStatement = new StringBuilder();
                            for (String line : statement.split("\n")) {
                                if (!line.trim().startsWith("--")) {
                                    cleanStatement.append(line).append("\n");
                                }
                            }
                            
                            String finalSql = cleanStatement.toString().trim();
                            if (!finalSql.isEmpty()) {
                                em.createNativeQuery(finalSql).executeUpdate();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de l'exécution de import.sql : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Base de données déjà initialisée (" + count + " utilisateurs trouvés).");
        }
    }
}
