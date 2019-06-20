package com.xabe.web.api.resource;

import com.xabe.web.api.domain.service.PersonService;
import com.xabe.web.api.exception.PersonNotFoundException;
import com.xabe.web.api.payload.PersonPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;

@RestController
@RequestMapping("/v1/persons")
public class PersonResource {
    public static final String APPLICATION_PROBLEM_JSON = "application/problem+json";
    private final PersonService personService;

    @Autowired
    public PersonResource(PersonService personService) {
        this.personService = personService;
    }

    @RequestMapping(method = OPTIONS, value = "/", produces = {APPLICATION_JSON_VALUE,APPLICATION_PROBLEM_JSON})
    public ResponseEntity optionsForPersonResource() {
        return ResponseEntity.ok().allow(HttpMethod.GET,HttpMethod.POST,HttpMethod.PATCH,HttpMethod.PUT,HttpMethod.DELETE).build();
    }

    @RequestMapping(method = GET, value = "/{personId}", produces = {APPLICATION_JSON_VALUE,APPLICATION_PROBLEM_JSON})
    public PersonPayload getPerson(@PathVariable(value="personId")String personId) {
        return this.personService.getPerson(personId).map(PersonPayload::new).orElseThrow(() -> new PersonNotFoundException(personId, ServletUriComponentsBuilder.fromCurrentRequest().build().toUri()));
    }


}
