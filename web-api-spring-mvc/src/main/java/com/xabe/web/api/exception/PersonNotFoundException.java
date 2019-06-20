package com.xabe.web.api.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;
import java.util.Map;

public class PersonNotFoundException extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create("http://localhost:21020/problems/person-not-found");

    public PersonNotFoundException(String personId, URI instance) {
        super(TYPE, "Person is not found", Status.NOT_FOUND,
                "Person with identifier '" + personId + "' is not found", instance,
                null, Map.of("id", personId));
    }
}
