package com.notgabs.corp.rest;

import com.notgabs.corp.model.PlanAction;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

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
        entity.nom = planAction.nom;
        entity.description = planAction.description;
        entity.dateDebut = planAction.dateDebut;
        entity.dateFin = planAction.dateFin;
        entity.responsable = planAction.responsable;
        entity.risque = planAction.risque;
        entity.tauxAvancement = planAction.tauxAvancement;
        return entity;
    }
}
