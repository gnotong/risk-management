package com.notgabs.corp.rest;

import com.notgabs.corp.model.Recommandation;
import com.notgabs.corp.service.RecommandationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import jakarta.annotation.security.RolesAllowed;

@Path("/api/recommandations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
public class RecommandationResource {

    @Inject
    RecommandationService recommandationService;

    @GET
    public List<Recommandation> listAll() {
        return recommandationService.listAll();
    }

    @GET
    @Path("/{id}")
    public Recommandation getById(@PathParam("id") UUID id) {
        return recommandationService.getById(id);
    }

    @POST
    public Response create(Recommandation ent) {
        Recommandation created = recommandationService.create(ent);
        return Response.ok(created).status(Response.Status.CREATED).build();
    }
}
