package com.notgabs.corp.rest;

import com.notgabs.corp.model.Audit;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/api/audits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuditResource {

    @GET
    public List<Audit> listAll() {
        return Audit.listAll();
    }

    @GET
    @Path("/{id}")
    public Audit getById(@PathParam("id") UUID id) {
        Audit audit = Audit.findById(id);
        if (audit == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return audit;
    }

    @POST
    @Transactional
    public Response create(Audit audit) {
        if (audit.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        audit.persist();
        return Response.ok(audit).status(201).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Audit update(@PathParam("id") UUID id, Audit audit) {
        Audit entity = Audit.findById(id);
        if (entity == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        entity.nom = audit.nom;
        entity.description = audit.description;
        entity.dateRealisation = audit.dateRealisation;
        entity.auditeur = audit.auditeur;
        entity.statutAudit = audit.statutAudit;
        return entity;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        Audit entity = Audit.findById(id);
        if (entity == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        entity.delete();
        return Response.status(204).build();
    }
}
