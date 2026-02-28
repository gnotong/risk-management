package com.notgabs.corp.rest;

import com.notgabs.corp.model.Risque;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.notgabs.corp.service.NotificationService;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import com.notgabs.corp.model.Incident;
import com.notgabs.corp.model.PlanAction;
import jakarta.annotation.security.RolesAllowed;

@Path("/api/risques")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
public class RisqueResource {

    @Inject
    NotificationService notificationService;

    @GET
    public List<Risque> listAll() {
        return Risque.listAll();
    }

    @GET
    @Path("/{id}")
    public Risque getById(@PathParam("id") UUID id) {
        Risque risque = Risque.findById(id);
        if (risque == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return risque;
    }

    @POST
    @Transactional
    public Response create(Risque risque) {
        if (risque.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        if (risque.proprietaire == null || risque.proprietaire.id == null) {
            throw new WebApplicationException("La définition d'un propriétaire est obligatoire lors de la création d'un risque.", Response.Status.BAD_REQUEST);
        }
        risque.persist();
        notificationService.notifsNouveauRisqueTresEleve(risque);
        return Response.ok(risque).status(201).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Risque update(@PathParam("id") UUID id, Risque risque) {
        Risque entity = Risque.findById(id);
        if (entity == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        if (entity.statut == com.notgabs.corp.model.StatutRisque.CLOTURE) {
            throw new WebApplicationException("Modification interdite : Impossible de modifier un risque clôturé.", Response.Status.BAD_REQUEST);
        }
        if (risque.proprietaire == null || risque.proprietaire.id == null) {
            throw new WebApplicationException("La définition d'un propriétaire est obligatoire.", Response.Status.BAD_REQUEST);
        }
        entity.libelle = risque.libelle;
        entity.description = risque.description;
        entity.probabilite = risque.probabilite;
        entity.gravite = risque.gravite;
        entity.statut = risque.statut;
        entity.proprietaire = risque.proprietaire;
        // score will be recalculated on PreUpdate
        notificationService.notifsNouveauRisqueTresEleve(entity);
        return entity;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        Risque entity = Risque.findById(id);
        if (entity == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        long incidentCount = Incident.count("risque", entity);
        long planCount = PlanAction.count("risque", entity);
        
        if (incidentCount > 0 || planCount > 0) {
            throw new WebApplicationException("Suppression interdite : Ce risque est lié à un Incident ou un Plan d'Action.", Response.Status.BAD_REQUEST);
        }
        
        entity.delete();
        return Response.status(204).build();
    }
}
