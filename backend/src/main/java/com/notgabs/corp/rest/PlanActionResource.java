package com.notgabs.corp.rest;

import com.notgabs.corp.model.PlanAction;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.notgabs.corp.model.SuiviPlanAction;

@Path("/api/planactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlanActionResource {

    @GET
    public List<PlanAction> listAll() {
        return PlanAction.listAll();
    }

    @POST
    @Transactional
    public Response create(PlanAction planAction) {
        if (planAction.dateDebut != null && planAction.dateFin != null) {
            if (!planAction.dateFin.isAfter(planAction.dateDebut)) {
                throw new WebApplicationException("La date de fin doit être strictement supérieure à la date de début.", Response.Status.BAD_REQUEST);
            }
        }
        planAction.persist();
        return Response.ok(planAction).status(201).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public PlanAction update(@PathParam("id") UUID id, PlanAction planAction) {
        PlanAction entity = PlanAction.findById(id);
        if (entity == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        if (planAction.dateDebut != null && planAction.dateFin != null) {
            if (!planAction.dateFin.isAfter(planAction.dateDebut)) {
                throw new WebApplicationException("La date de fin doit être strictement supérieure à la date de début.", Response.Status.BAD_REQUEST);
            }
        }
        
        // Tracking changes
        boolean changed = false;
        StringBuilder changes = new StringBuilder("Mise à jour: ");
        if (entity.tauxAvancement != planAction.tauxAvancement) {
            changes.append("Avancement de ").append(entity.tauxAvancement).append("% à ").append(planAction.tauxAvancement).append("%. ");
            changed = true;
        }

        entity.nom = planAction.nom;
        entity.description = planAction.description;
        entity.dateDebut = planAction.dateDebut;
        entity.dateFin = planAction.dateFin;
        entity.responsable = planAction.responsable;
        entity.risque = planAction.risque;
        entity.tauxAvancement = planAction.tauxAvancement;
        
        if (changed || changes.toString().equals("Mise à jour: ")) {
            SuiviPlanAction suivi = new SuiviPlanAction();
            suivi.planAction = entity;
            suivi.commentaire = changed ? changes.toString() : "Mise à jour générale des informations.";
            suivi.dateSuivi = LocalDateTime.now();
            suivi.persist();
        }

        return entity;
    }
}
