package com.notgabs.corp.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class DomainExceptionMapper implements ExceptionMapper<RuntimeException> {

    private static final Logger LOG = Logger.getLogger(DomainExceptionMapper.class);

    @Override
    public Response toResponse(RuntimeException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.message = exception.getMessage();

        Response.Status status;

        if (exception instanceof BusinessException) {
            status = Response.Status.BAD_REQUEST;
            errorResponse.code = "BAD_REQUEST";
        } else if (exception instanceof NotFoundException) {
            status = Response.Status.NOT_FOUND;
            errorResponse.code = "NOT_FOUND";
        } else if (exception instanceof ForbiddenException) {
            status = Response.Status.FORBIDDEN;
            errorResponse.code = "FORBIDDEN";
        } else if (exception instanceof UnauthorizedException) {
            status = Response.Status.UNAUTHORIZED;
            errorResponse.code = "UNAUTHORIZED";
        } else if (exception instanceof ConflictException) {
            status = Response.Status.CONFLICT;
            errorResponse.code = "CONFLICT";
        } else {
            // Rethrow or handle generic runtime exceptions if needed, 
            // but we can map them to 500 for safety if it's unexpected here.
            LOG.error("Unhandled RuntimeException in DomainExceptionMapper", exception);
            status = Response.Status.INTERNAL_SERVER_ERROR;
            errorResponse.code = "INTERNAL_ERROR";
            errorResponse.message = "An unexpected error occurred.";
        }

        return Response.status(status).entity(errorResponse).build();
    }
}
