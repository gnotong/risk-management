package com.notgabs.corp.rest;

import com.notgabs.corp.dto.CreateUserRequest;
import com.notgabs.corp.dto.UpdateUserRequest;
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
@RolesAllowed("ADMIN")
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

    /**
     * Create user with password and sync to Keycloak (Admin only)
     */
    @POST
    @Path("/register")
    @RolesAllowed("ADMIN")
    public Response createUser(CreateUserRequest request) {
        Utilisateur created = utilisateurService.createWithKeycloak(request);
        return Response.ok(created).status(Response.Status.CREATED).build();
    }

    /**
     * Update user with Keycloak sync (Admin only)
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response updateUser(@PathParam("id") UUID id, UpdateUserRequest request) {
        Utilisateur updated = utilisateurService.updateWithKeycloak(id, request);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response delete(@PathParam("id") UUID id) {
        utilisateurService.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
