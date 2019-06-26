package com.xabe.web.api.resource;

import com.xabe.web.api.domain.service.PersonService;
import com.xabe.web.api.exception.PersonNotFoundException;
import com.xabe.web.api.payload.PersonPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import javax.validation.Valid;
import java.net.URI;
import java.util.function.Supplier;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/v1/persons")
public class PersonResource {
    public static final String APPLICATION_PROBLEM_JSON = "application/problem+json";
    public static final MediaType APPLICATION_PROBLEM_JSON_TYPE = new MediaType("application", "problem+json");
    private final PersonService personService;

    @Autowired
    public PersonResource(PersonService personService) {
        this.personService = personService;
    }

    @RequestMapping(method = OPTIONS, produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON})
    public ResponseEntity optionsForPersonResource() {
        return ResponseEntity.ok().allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.PATCH, HttpMethod.PUT, HttpMethod.DELETE).build();
    }

    @RequestMapping(method = GET, value = "/{personId}", produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON})
    public PersonPayload getPerson(@PathVariable(value = "personId") String personId) {
        return this.personService.getPerson(personId).map(PersonPayload::new).orElseThrow(() -> new PersonNotFoundException(personId, ServletUriComponentsBuilder.fromCurrentRequest().build().toUri()));
    }

    @RequestMapping(method = POST, consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON})
    public ResponseEntity createPerson(@RequestBody @Valid PersonPayload personPayload) {
        return this.personService.createPerson(personPayload.toPerson()).
                map(this::createResponseSuccessCreate).
                orElseGet(createResponseErrorCreate(personPayload.personId));
    }

    private ResponseEntity createResponseSuccessCreate(String personId) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/{personId}").buildAndExpand(personId).toUri());
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }

    private Supplier<ResponseEntity> createResponseErrorCreate(String personId) {
        return () -> {
            final HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(APPLICATION_PROBLEM_JSON_TYPE);
            final ThrowableProblem problem = Problem
                    .builder()
                    .withType(URI.create("http://localhost:21020/problems/non-unique-person"))
                    .withInstance(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri())
                    .withStatus(Status.BAD_REQUEST)
                    .withTitle("The person is not unique")
                    .withDetail("Error create person")
                    .with("personId", personId)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(httpHeaders).body(problem);
        };
    }
}
