package com.notgabs.corp.rest;

import com.notgabs.corp.model.Incident;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/api/incidents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IncidentResource {

    @GET
    public List<Incident> listAll() {
        return Incident.listAll();
    }

    @GET
    @Path("/{id}")
    public Incident getById(@PathParam("id") UUID id) {
        Incident entity = Incident.findById(id);
        if (entity == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return entity;
    }

    @POST
    @Transactional
    public Response create(Incident ent) {
        if (ent.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        ent.persist();
        return Response.ok(ent).status(201).build();
    }
}
