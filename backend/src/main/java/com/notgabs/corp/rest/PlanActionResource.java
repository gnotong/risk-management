package com.notgabs.corp.rest;

import com.notgabs.corp.model.PlanAction;
import com.notgabs.corp.model.SuiviPlanAction;
import com.notgabs.corp.service.PlanActionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import jakarta.annotation.security.RolesAllowed;

@Path("/api/planactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
public class PlanActionResource {

    @Inject
    PlanActionService planActionService;

    @GET
    public List<PlanAction> listAll() {
        return planActionService.listAll();
    }

    @GET
    @Path("/{id}")
    public PlanAction getById(@PathParam("id") UUID id) {
        return planActionService.getById(id);
    }

    @POST
    public Response create(@Valid PlanAction planAction) {
        PlanAction created = planActionService.create(planAction);
        return Response.ok(created).status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public PlanAction update(@PathParam("id") UUID id, @Valid PlanAction planAction, @HeaderParam("X-User-Id") String userIdstr) {
        return planActionService.update(id, planAction, userIdstr);
    }

    @GET
    @Path("/{id}/suivis")
    public List<SuiviPlanAction> getSuivis(@PathParam("id") UUID id) {
        return planActionService.getSuivis(id);
    }

    @POST
    @Path("/{id}/suivis")
    public Response addSuivi(@PathParam("id") UUID id, SuiviPlanAction suivi) {
        SuiviPlanAction created = planActionService.addSuivi(id, suivi);
        return Response.ok(created).status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        planActionService.delete(id);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}/suivis/{suiviId}")
    public Response deleteSuivi(@PathParam("id") UUID id, @PathParam("suiviId") UUID suiviId) {
        planActionService.deleteSuivi(id, suiviId);
        return Response.noContent().build();
    }
}
