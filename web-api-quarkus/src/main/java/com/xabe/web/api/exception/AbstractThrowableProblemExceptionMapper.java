package com.xabe.web.api.exception;

import com.xabe.web.api.config.MediaTypeExt;
import org.zalando.problem.AbstractThrowableProblem;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AbstractThrowableProblemExceptionMapper implements ExceptionMapper<AbstractThrowableProblem> {

    @Override
    public Response toResponse(AbstractThrowableProblem ex) {
        return Response
                .status(ex.getStatus().getStatusCode())
                .entity(ex)
                .type(MediaTypeExt.APPLICATION_PROBLEM_JSON)
                .build();
    }
}
