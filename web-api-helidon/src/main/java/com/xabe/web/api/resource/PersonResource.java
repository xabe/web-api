package com.xabe.web.api.resource;

import com.xabe.web.api.domain.service.PersonService;
import com.xabe.web.api.exception.PersonNotFoundException;
import com.xabe.web.api.payload.PersonPayload;
import io.helidon.common.http.Http;
import io.helidon.common.http.MediaType;
import io.helidon.webserver.*;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.net.URI;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;


public class PersonResource implements Service {
    public static final String APPLICATION_PROBLEM_JSON = "application/problem+json";
    private final PersonService personService;
    private final Validator validator;

    public PersonResource(Validator validator, PersonService personService) {
        this.validator = validator;
        this.personService = personService;
    }

    @Override
    public void update(Routing.Rules rules) {
        rules.
                options("/v1/persons", this::getOptions).
                get("/v1/persons/{personId}", this::getPersons).
                post("/v1/persons", Handler.create(PersonPayload.class, this::createPersons));

    }

    protected void getOptions(ServerRequest serverRequest, ServerResponse serverResponse) {
        serverResponse.headers().putIfAbsent(Http.Header.ALLOW, Http.Method.GET.name(), Http.Method.POST.name(), Http.Method.PUT.name(), Http.Method.DELETE.name());
        serverResponse.headers().putIfAbsent(Http.Header.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        serverResponse.headers().putIfAbsent(Http.Header.CONTENT_LENGTH,"0");
        serverResponse.status(Http.Status.OK_200);
        serverResponse.send();
    }

    protected void getPersons(ServerRequest serverRequest, ServerResponse serverResponse) {
        final String personId = serverRequest.path().param("personId");
        this.personService.getPerson(personId).map( person -> {
            serverResponse.headers().putIfAbsent(Http.Header.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
            return serverResponse.status(Http.Status.OK_200).send(new PersonPayload(person));
        }).orElseThrow(() -> new PersonNotFoundException(personId, serverRequest.uri()));
    }

    protected void createPersons(ServerRequest serverRequest, ServerResponse serverResponse, PersonPayload personPayload) {
        final Set<ConstraintViolation<PersonPayload>> constraintViolations = this.validator.validate(personPayload);
        if(constraintViolations.isEmpty()){
            this.personService.createPerson(personPayload.toPerson())
                    .map(createResponseSuccess(serverRequest, serverResponse))
                    .orElseGet(createResponseError(serverRequest,serverResponse,personPayload.personId));
        }
        else{
            throw new ConstraintViolationException(constraintViolations);
        }

    }

    protected Function<String,Void> createResponseSuccess(ServerRequest serverRequest, ServerResponse serverResponse) {
        return personId ->{
            serverResponse.status(Http.Status.CREATED_201);
            serverResponse.headers().putIfAbsent(Http.Header.LOCATION,serverRequest.path().param(personId));
            serverResponse.headers().putIfAbsent(Http.Header.CONTENT_TYPE,MediaType.APPLICATION_JSON.toString());
            serverResponse.send();
            return null;
        };
    }

    protected Supplier<Void> createResponseError(ServerRequest serverRequest, ServerResponse serverResponse, String personId) {
        return () ->{
            final ThrowableProblem problem = Problem
                    .builder()
                    .withType(URI.create("http://localhost:21020/problems/non-unique-person"))
                    .withInstance(serverRequest.uri())
                    .withStatus(Status.BAD_REQUEST)
                    .withTitle("The person is not unique")
                    .withDetail("Error create person")
                    .with("personId", personId)
                    .build();
            serverResponse.headers().putIfAbsent(Http.Header.CONTENT_TYPE,APPLICATION_PROBLEM_JSON);
            serverResponse.status(Http.Status.BAD_REQUEST_400);
            serverResponse.send(problem);
            return null;
        };
    }
}
