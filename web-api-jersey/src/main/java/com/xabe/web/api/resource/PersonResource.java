package com.xabe.web.api.resource;

import com.xabe.web.api.config.MediaTypeExt;
import com.xabe.web.api.domain.model.Person;
import com.xabe.web.api.domain.service.PersonService;
import com.xabe.web.api.exception.PersonNotFoundException;
import com.xabe.web.api.payload.PersonPayload;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.function.Function;
import java.util.function.Supplier;

@Path("/v1/persons")
@Singleton
@Consumes(MediaTypeExt.APPLICATION_JSON)
@Produces({MediaTypeExt.APPLICATION_JSON,MediaTypeExt.APPLICATION_PROBLEM_JSON})
public class PersonResource {

    private final PersonService personService;

    @Inject
    public PersonResource(PersonService personService) {
        this.personService = personService;
    }


    @OPTIONS
    public Response optionsForPersonResource() {
        return Response.status(Response.Status.OK)
                .header(HttpHeaders.ALLOW,"POST, PUT, GET, PATCH, DELETE")
                .header(HttpHeaders.CONTENT_TYPE, MediaTypeExt.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_LENGTH, "0")
                .build();
    }

    @Path("/{personId}")
    @GET
    public PersonPayload getPerson(@PathParam("personId") String personId, @Context UriInfo uriInfo) {
        return this.personService.getPerson(personId).map(PersonPayload::new).orElseThrow(() -> new PersonNotFoundException(personId, uriInfo.getRequestUri()));
    }

    @POST
    public Response createPerson(@Valid PersonPayload personPayload, @Context UriInfo uriInfo) {
        return this.personService.createPerson(personPayload.toPerson())
                .map(createResponseSuccessCreate(uriInfo))
                .orElseGet(createResponseErrorCreate(personPayload.personId, uriInfo));
    }

    private Function<String,Response> createResponseSuccessCreate(UriInfo uriInfo) {
        return personId ->  Response.created(uriInfo.getRequestUriBuilder().path(personId).build()).build();
    }

    private Supplier<Response> createResponseErrorCreate(String personId, UriInfo uriInfo) {
        return () ->
            Response
                    .status(Response.Status.BAD_REQUEST)
                    .type(MediaTypeExt.APPLICATION_PROBLEM_JSON)
                    .entity(Problem
                            .builder()
                            .withType(URI.create("http://localhost:21020/problems/non-unique-person"))
                            .withInstance(uriInfo.getRequestUri())
                            .withStatus(Status.BAD_REQUEST)
                            .withTitle("The person is not unique")
                            .withDetail("Error create person")
                            .with("personId",personId)
                            .build())
                    .build();
    }

    @Path("/{personId}")
    @PATCH
    public Response patchPerson(@PathParam("personId") String personId, PersonPayload personPayload, @Context  UriInfo uriInfo) {
        return this.personService.patchPerson(personId, personPayload.toPerson()).
                map(id -> Response.noContent().location(uriInfo.getRequestUriBuilder().build()).build())
                .orElseThrow(() -> new PersonNotFoundException(personId, uriInfo.getRequestUri()));
    }


    @Path("/{personId}")
    @PUT
    public Response updatePerson(@PathParam("personId") String personId, PersonPayload personPayload, @Context UriInfo uriInfo) {
        return this.personService.updatePerson(personId, personPayload.toPerson()).
                map(id -> Response.noContent().location(uriInfo.getRequestUriBuilder().build()).build())
                .orElseThrow(() -> new PersonNotFoundException(personId, uriInfo.getRequestUri()));
    }

    @Path("/{personId}")
    @DELETE
    public Response deletePerson(@PathParam("personId")String personId, @Context UriInfo uriInfo) {
        return this.personService.deletePerson(personId).
                map(id -> Response.noContent().location(uriInfo.getRequestUriBuilder().build()).build())
                .orElseThrow(() -> new PersonNotFoundException(personId, uriInfo.getRequestUri()));
    }
}
