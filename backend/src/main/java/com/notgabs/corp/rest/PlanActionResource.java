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
import com.notgabs.corp.model.StatutPlanAction;
import com.notgabs.corp.model.Utilisateur;
import com.notgabs.corp.model.Role;
import jakarta.annotation.security.RolesAllowed;

@Path("/api/planactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
public class PlanActionResource {

    @GET
    public List<PlanAction> listAll() {
        return PlanAction.listAll();
    }

    @GET
    @Path("/{id}")
    public PlanAction getById(@PathParam("id") UUID id) {
        PlanAction entity = PlanAction.findById(id);
        if (entity == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return entity;
    }

    @POST
    @Transactional
    public Response create(PlanAction planAction) {
        if (planAction.statut == null) {
            planAction.statut = StatutPlanAction.NON_COMMENCE;
        }
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
    public PlanAction update(@PathParam("id") UUID id, PlanAction planAction, @HeaderParam("X-User-Id") String userIdstr) {
        PlanAction entity = PlanAction.findById(id);
        if (entity == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        // Access Control
        if (planAction.tauxAvancement != entity.tauxAvancement) {
            if (userIdstr == null || userIdstr.isEmpty()) {
                throw new WebApplicationException("Non autorisé : Identifiant utilisateur manquant.", Response.Status.UNAUTHORIZED);
            }
            if ("admin-override".equals(userIdstr)) {
                // Allow simulated admin
            } else {
                try {
                    UUID userId = UUID.fromString(userIdstr);
                    Utilisateur user = Utilisateur.findById(userId);
                    if (user == null || (user.role != Role.ADMIN && (entity.responsable == null || !entity.responsable.id.equals(userId)))) {
                        throw new WebApplicationException("Accès refusé : Seul l'ADMIN ou le responsable peut modifier l'avancement.", Response.Status.FORBIDDEN);
                    }
                } catch (IllegalArgumentException e) {
                    throw new WebApplicationException("Format d'identifiant utilisateur invalide.", Response.Status.BAD_REQUEST);
                }
            }
        }

        // Constraints Dates
        if (planAction.dateDebut != null && planAction.dateFin != null) {
            if (!planAction.dateFin.isAfter(planAction.dateDebut)) {
                throw new WebApplicationException("La date de fin doit être strictement supérieure à la date de début.", Response.Status.BAD_REQUEST);
            }
        }
        
        // Constraint Status
        if (planAction.statut == StatutPlanAction.TERMINE && planAction.tauxAvancement < 100) {
            throw new WebApplicationException("Clôture interdite : L'avancement doit être à 100% pour terminer le plan.", Response.Status.BAD_REQUEST);
        }
        
        // Auto-close logic on 100%
        if (planAction.tauxAvancement == 100) {
            planAction.statut = StatutPlanAction.TERMINE;
            if (entity.risque != null) {
                entity.risque.statut = com.notgabs.corp.model.StatutRisque.CLOTURE;
                entity.risque.persist();
            }
        }
        
        // Tracking changes
        boolean changedAvancement = false;
        StringBuilder changes = new StringBuilder("Mise à jour: ");
        if (entity.tauxAvancement != planAction.tauxAvancement) {
            changes.append("Avancement de ").append(entity.tauxAvancement).append("% à ").append(planAction.tauxAvancement).append("%. ");
            changedAvancement = true;
        }
        if (entity.statut != planAction.statut) {
            changes.append("Statut passé à ").append(planAction.statut).append(". ");
        }

        entity.nom = planAction.nom;
        entity.description = planAction.description;
        entity.dateDebut = planAction.dateDebut;
        entity.dateFin = planAction.dateFin;
        entity.responsable = planAction.responsable;
        entity.risque = planAction.risque;
        entity.tauxAvancement = planAction.tauxAvancement;
        entity.statut = planAction.statut;
        
        if (changedAvancement || changes.toString().length() > 13) {
            SuiviPlanAction suivi = new SuiviPlanAction();
            suivi.planAction = entity;
            suivi.commentaire = changes.toString();
            suivi.dateSuivi = LocalDateTime.now();
            suivi.persist();
        }

        return entity;
    }

    @GET
    @Path("/{id}/suivis")
    public List<SuiviPlanAction> getSuivis(@PathParam("id") UUID id) {
        PlanAction plan = PlanAction.findById(id);
        if (plan == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return SuiviPlanAction.find("planAction = ?1 order by dateSuivi desc", plan).list();
    }

    @POST
    @Path("/{id}/suivis")
    @Transactional
    public Response addSuivi(@PathParam("id") UUID id, SuiviPlanAction suivi) {
        PlanAction plan = PlanAction.findById(id);
        if (plan == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        suivi.planAction = plan;
        suivi.dateSuivi = LocalDateTime.now();
        suivi.persist();
        return Response.ok(suivi).status(201).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        PlanAction entity = PlanAction.findById(id);
        if (entity == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        if (entity.statut == StatutPlanAction.TERMINE) {
            throw new WebApplicationException("Suppression interdite : Impossible de supprimer un plan d'action terminé.", Response.Status.BAD_REQUEST);
        }
        
        entity.delete();
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}/suivis/{suiviId}")
    @Transactional
    public Response deleteSuivi(@PathParam("id") UUID id, @PathParam("suiviId") UUID suiviId) {
        PlanAction plan = PlanAction.findById(id);
        if (plan == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        if (plan.statut == StatutPlanAction.TERMINE) {
            throw new WebApplicationException("Suppression interdite : Impossible de supprimer les journaux d'un plan d'action terminé.", Response.Status.BAD_REQUEST);
        }
        
        SuiviPlanAction suivi = SuiviPlanAction.findById(suiviId);
        if (suivi == null || !suivi.planAction.id.equals(id)) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        suivi.delete();
        return Response.noContent().build();
    }
}
