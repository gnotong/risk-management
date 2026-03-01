package com.notgabs.corp.rest;

import com.notgabs.corp.model.Incident;
import com.notgabs.corp.service.IncidentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import jakarta.annotation.security.RolesAllowed;

@Path("/api/incidents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
public class IncidentResource {

    @Inject
    IncidentService incidentService;

    @GET
    public List<Incident> listAll() {
        return incidentService.listAll();
    }

    @GET
    @Path("/{id}")
    public Incident getById(@PathParam("id") UUID id) {
        return incidentService.getById(id);
    }

    @POST
    public Response create(Incident ent) {
        Incident created = incidentService.create(ent);
        return Response.ok(created).status(Response.Status.CREATED).build();
    }
}
