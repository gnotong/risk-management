package com.notgabs.corp.rest;

import com.notgabs.corp.model.Utilisateur;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import com.notgabs.corp.model.Risque;
import com.notgabs.corp.model.PlanAction;
import jakarta.annotation.security.RolesAllowed;

@Path("/api/utilisateurs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
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

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        Utilisateur entity = Utilisateur.findById(id);
        if (entity == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        long riskCount = Risque.count("proprietaire", entity);
        long planCount = PlanAction.count("responsable", entity);
        
        if (riskCount > 0 || planCount > 0) {
            throw new WebApplicationException("Suppression interdite : Cet utilisateur est responsable d'un Risque ou d'un Plan d'Action.", Response.Status.BAD_REQUEST);
        }
        
        entity.delete();
        return Response.status(204).build();
    }
}
