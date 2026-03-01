package com.notgabs.corp.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    private static final Logger LOG = Logger.getLogger(WebApplicationExceptionMapper.class);

    @Override
    public Response toResponse(WebApplicationException e) {
        LOG.warn("WebApplicationException interceptée : " + e.getMessage());
        return Response.status(e.getResponse().getStatus())
                .entity(new ErrorResponse("WEB_APPLICATION_ERROR", e.getMessage()))
                .build();
    }
}
