package com.notgabs.corp.rest;

import com.notgabs.corp.model.Risque;
import com.notgabs.corp.service.RisqueService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import jakarta.annotation.security.RolesAllowed;

@Path("/api/risques")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
public class RisqueResource {

    @Inject
    RisqueService risqueService;

    @GET
    public List<Risque> listAll() {
        return risqueService.listAll();
    }

    @GET
    @Path("/{id}")
    public Risque getById(@PathParam("id") UUID id) {
        return risqueService.getById(id);
    }

    @POST
    public Response create(Risque risque) {
        Risque created = risqueService.create(risque);
        return Response.ok(created).status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Risque update(@PathParam("id") UUID id, Risque risque) {
        return risqueService.update(id, risque);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        risqueService.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
