package com.notgabs.corp.rest;

import com.notgabs.corp.model.Recommandation;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/api/recommandations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RecommandationResource {

    @GET
    public List<Recommandation> listAll() {
        return Recommandation.listAll();
    }

    @GET
    @Path("/{id}")
    public Recommandation getById(@PathParam("id") UUID id) {
        Recommandation entity = Recommandation.findById(id);
        if (entity == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return entity;
    }

    @POST
    @Transactional
    public Response create(Recommandation ent) {
        if (ent.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        ent.persist();
        return Response.ok(ent).status(201).build();
    }
}
