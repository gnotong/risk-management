package com.notgabs.corp.rest;

import com.notgabs.corp.model.Audit;
import com.notgabs.corp.service.AuditService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import jakarta.annotation.security.RolesAllowed;

@Path("/api/audits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
public class AuditResource {

    @Inject
    AuditService auditService;

    @GET
    public List<Audit> listAll() {
        return auditService.listAll();
    }

    @GET
    @Path("/{id}")
    public Audit getById(@PathParam("id") UUID id) {
        return auditService.getById(id);
    }

    @POST
    public Response create(Audit audit) {
        Audit created = auditService.create(audit);
        return Response.ok(created).status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Audit update(@PathParam("id") UUID id, Audit audit) {
        return auditService.update(id, audit);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        auditService.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
