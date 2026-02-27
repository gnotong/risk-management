package com.notgabs.corp.rest;

import com.notgabs.corp.model.Utilisateur;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/api/utilisateurs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UtilisateurResource {

    @GET
    public List<Utilisateur> listAll() {
        return Utilisateur.listAll();
    }

    @GET
    @Path("/{id}")
    public Utilisateur getById(@PathParam("id") UUID id) {
        Utilisateur user = Utilisateur.findById(id);
        if (user == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return user;
    }

    @POST
    @Transactional
    public Response create(Utilisateur user) {
        if (user.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        user.persist();
        return Response.ok(user).status(201).build();
    }
}
