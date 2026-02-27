package com.notgabs.corp.service;

import com.notgabs.corp.model.Risque;
import com.notgabs.corp.model.PlanAction;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class NotificationService {

    @Inject
    Mailer mailer;

    public void notifsNouveauRisqueTresEleve(Risque risque) {
        if (risque.score >= 6 && risque.proprietaire != null && risque.proprietaire.email != null) {
            mailer.send(
                Mail.withText(risque.proprietaire.email, 
                              "Alerte: Nouveau Risque Très Élevé",
                              "Un nouveau risque très élevé a été identifié: " + risque.libelle + "\nScore: " + risque.score)
            );
        }
    }

    public void notifsRetardPlanAction(PlanAction planAction) {
        if (planAction.responsable != null && planAction.responsable.email != null) {
            mailer.send(
                Mail.withText(planAction.responsable.email, 
                              "Alerte: Retard sur le Plan d'Action",
                              "Votre plan d'action '" + planAction.nom + "' est en retard.\nMerci de le mettre à jour.")
            );
        }
    }
}
