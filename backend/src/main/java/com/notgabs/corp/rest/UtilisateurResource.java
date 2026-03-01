package com.notgabs.corp.rest;

import com.notgabs.corp.model.Utilisateur;
import com.notgabs.corp.service.UtilisateurService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import jakarta.annotation.security.RolesAllowed;

@Path("/api/utilisateurs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
public class UtilisateurResource {

    @Inject
    UtilisateurService utilisateurService;

    @GET
    public List<Utilisateur> listAll() {
        return utilisateurService.listAll();
    }

    @GET
    @Path("/{id}")
    public Utilisateur getById(@PathParam("id") UUID id) {
        return utilisateurService.getById(id);
    }

    @POST
    public Response create(Utilisateur user) {
        Utilisateur created = utilisateurService.create(user);
        return Response.ok(created).status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        utilisateurService.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
