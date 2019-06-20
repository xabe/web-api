package com.xabe.web.api.exception;

import com.xabe.web.api.config.MediaTypeExt;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {
    @Context
    private UriInfo uriInfo;

    ValidationExceptionMapper(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public ValidationExceptionMapper() {

    }

    @Override
    public Response toResponse(final ValidationException ex) {
        if (ex instanceof ConstraintViolationException) {
            final ConstraintViolationException constraint = (ConstraintViolationException) ex;

            final ThrowableProblem problem = Problem
                    .builder()
                    .withType(URI.create("http://localhost:21020/problems/invalid-parameters"))
                    .withTitle("One or more request parameters are not valid")
                    .withStatus(Status.BAD_REQUEST)
                    .withInstance(uriInfo.getRequestUri())
                    .with("invalid-parameters", constraint
                            .getConstraintViolations()
                            .stream()
                            .map(this::buildViolation)
                            .collect(Collectors.toList()))
                    .build();

            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .type(MediaTypeExt.APPLICATION_PROBLEM_JSON)
                    .entity(problem)
                    .build();
        }

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaTypeExt.APPLICATION_PROBLEM_JSON)
                .entity(Problem
                        .builder()
                        .withTitle("The server is not able to process the request")
                        .withType(URI.create("http://localhost:21020/problems/server-error"))
                        .withInstance(uriInfo.getRequestUri())
                        .withStatus(Status.INTERNAL_SERVER_ERROR)
                        .withDetail(ex.getMessage())
                        .build())
                .build();
    }

    protected Map<?, ?> buildViolation(ConstraintViolation<?> violation) {
        return Map.of(
                "bean", violation.getRootBeanClass().getName(),
                "property", violation.getPropertyPath().toString(),
                "reason", violation.getMessage(),
                "value", Objects.requireNonNullElse(violation.getInvalidValue(), "null")
        );
    }
}