package com.notgabs.corp.rest;

import com.notgabs.corp.model.Incident;
import com.notgabs.corp.model.SuiviIncident;
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
@RolesAllowed({"USER", "ADMIN"})
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

    @PUT
    @Path("/{id}")
    public Incident update(@PathParam("id") UUID id, Incident incident) {
        return incidentService.update(id, incident);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        incidentService.delete(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/suivis")
    public List<SuiviIncident> getSuivis(@PathParam("id") UUID id) {
        return incidentService.getSuivis(id);
    }

    @POST
    @Path("/{id}/suivis")
    public Response addSuivi(@PathParam("id") UUID id, SuiviIncident suivi) {
        SuiviIncident created = incidentService.addSuivi(id, suivi);
        return Response.ok(created).status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("/{id}/suivis/{suiviId}")
    public Response deleteSuivi(@PathParam("id") UUID id, @PathParam("suiviId") UUID suiviId) {
        incidentService.deleteSuivi(id, suiviId);
        return Response.noContent().build();
    }
}
