package com.xabe.web.api.resource;

import com.xabe.web.api.domain.model.Person;
import com.xabe.web.api.domain.service.PersonService;
import com.xabe.web.api.exception.PersonNotFoundException;
import com.xabe.web.api.payload.PersonPayload;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PersonResourceTest {

    private PersonService personService;

    private PersonResource personResource;

    @BeforeEach
    public void setUp() throws Exception {
        this.personService = mock(PersonService.class);
        this.personResource = new PersonResource(personService);
    }
    
    @Test
    public void shouldReturnResponseWithOptionsPerson() throws Exception {
        //Given
        
        //When
        final Response result = this.personResource.optionsForPersonResource();

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.getAllowedMethods(), is(Set.of("POST", "PUT", "GET","PATCH", "DELETE")));
    
    }

    @Test
    public void givenAPersonIdWhenInvokeGetPersonThenReturnPersonPayload() throws Exception {
        //Given
        final String personId = "47518419F";
        final Person person = Person.of("test","test", "1f");
        when(personService.getPerson(eq(personId))).thenReturn(Optional.of(person));

        //When
        final PersonPayload result = this.personResource.getPerson(personId, null);
        
        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.getName(), is(person.getName()));
        assertThat(result.getPersonId(), is(person.getPersonId()));
        assertThat(result.getSurname(), is(person.getSurname()));
    }

    @Test
    public void givenAPersonIdWhenInvokeGetPersonThenReturnPersonNotFoundException() throws Exception {
        //Given
        final String personId = "47518419F";
        final UriInfo uriInfo = mock(UriInfo.class);
        when(personService.getPerson(eq(personId))).thenReturn(Optional.empty());

        //When
        final PersonNotFoundException personNotFoundException = Assertions.assertThrows(PersonNotFoundException.class, () -> this.personResource.getPerson(personId, uriInfo));


        //Then
        assertThat(personNotFoundException,is(notNullValue()));
        assertThat(personNotFoundException.getStatus(),is(Status.NOT_FOUND));
        assertThat(personNotFoundException.getTitle(),is("Person is not found"));
        assertThat(personNotFoundException.getDetail(),is("Person with identifier '" + personId + "' is not found"));
        assertThat(personNotFoundException.getCause(),is(nullValue()));
        assertThat(personNotFoundException.getParameters(),is(Map.of("personId",personId)));

    }
    
    @Test
    public void givenAPersonPayloadWhenInvokeCreatePersonThenReturnResponseOk() throws Exception {
        //Given
        final UriInfo uriInfo = mock(UriInfo.class);
        final UriBuilder uriBuilder = new JerseyUriBuilder();
        final PersonPayload personPayload = new PersonPayload("","","");
        when(personService.createPerson(any())).thenReturn(Optional.of("id"));
        when(uriInfo.getRequestUriBuilder()).thenReturn(uriBuilder);

        //When
        final Response result = this.personResource.createPerson(personPayload, uriInfo);

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.getLocation(), is(notNullValue()));
        assertThat(result.getStatus(), is(Response.Status.CREATED.getStatusCode()));
        assertThat(result.getLocation(), is(notNullValue()));
        assertThat(result.getLocation(), is(new JerseyUriBuilder().path("id").build()));
    }

    @Test
    public void givenAPersonPayloadWhenInvokeCreatePersonThenReturnResponseBadRequest() throws Exception {
        //Given
        final UriInfo uriInfo = mock(UriInfo.class);
        final URI uri = URI.create("http://localhost:9000/api/persons/");
        final PersonPayload personPayload = new PersonPayload("","","1");
        when(personService.createPerson(any())).thenReturn(Optional.empty());
        when(uriInfo.getRequestUri()).thenReturn(uri);

        //When
        final Response result = this.personResource.createPerson(personPayload, uriInfo);

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.getLocation(), is(nullValue()));
        assertThat(result.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
        final ThrowableProblem problem = ThrowableProblem.class.cast(result.getEntity());
        assertThat(problem, is(notNullValue()));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getTitle(),is("The person is not unique"));
        assertThat(problem.getDetail(),is("Error create person"));
        assertThat(problem.getCause(),is(nullValue()));
        assertThat(problem.getParameters(),is(Map.of("personId","1")));
    }
    
    @Test
    public void givenAPersonPayloadAndPersonIdWhenInvokePatchPersonThenReturnResponsePerson() throws Exception {
        //Given
        final UriInfo uriInfo = mock(UriInfo.class);
        final UriBuilder uriBuilder = new JerseyUriBuilder().path("id");
        final String personId = "id";
        final PersonPayload personPayload = new PersonPayload("","","");
        when(personService.patchPerson(eq(personId),any())).thenReturn(Optional.of(personId));
        when(uriInfo.getRequestUriBuilder()).thenReturn(uriBuilder);

        //When
        final Response result = this.personResource.patchPerson(personId, personPayload,uriInfo);
        
        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        assertThat(result.getLocation(), is(notNullValue()));
        assertThat(result.getLocation(), is(new JerseyUriBuilder().path(personId).build()));
    }

    @Test
    public void givenAPersonPayloadAndPersonIdWhenInvokePatchPersonThenReturnResponseNotFoundPerson() throws Exception {
        //Given
        final UriInfo uriInfo = mock(UriInfo.class);
        final String personId = "id";
        final PersonPayload personPayload = new PersonPayload("","","");
        when(personService.patchPerson(eq(personId),any())).thenReturn(Optional.empty());

        //When
        final PersonNotFoundException personNotFoundException = Assertions.assertThrows(PersonNotFoundException.class, () -> this.personResource.patchPerson(personId, personPayload, uriInfo));


        //Then
        assertThat(personNotFoundException,is(notNullValue()));
        assertThat(personNotFoundException.getStatus(),is(Status.NOT_FOUND));
        assertThat(personNotFoundException.getTitle(),is("Person is not found"));
        assertThat(personNotFoundException.getDetail(),is("Person with identifier '" + personId + "' is not found"));
        assertThat(personNotFoundException.getCause(),is(nullValue()));
        assertThat(personNotFoundException.getParameters(),is(Map.of("personId",personId)));
    }


    @Test
    public void givenAPersonPayloadAndPersonIdWhenInvokeUpdatePersonThenReturnResponse() throws Exception {
        //Given
        final UriInfo uriInfo = mock(UriInfo.class);
        final UriBuilder uriBuilder = new JerseyUriBuilder().path("id");
        final String personId = "id";
        final PersonPayload personPayload = new PersonPayload("","","");
        when(personService.updatePerson(eq(personId),any())).thenReturn(Optional.of(personId));
        when(uriInfo.getRequestUriBuilder()).thenReturn(uriBuilder);

        //When
        final Response result = this.personResource.updatePerson(personId, personPayload,uriInfo);

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        assertThat(result.getLocation(), is(notNullValue()));
        assertThat(result.getLocation(), is(new JerseyUriBuilder().path(personId).build()));
    }

    @Test
    public void givenAPersonPayloadAndPersonIdWhenInvokeUpdatePersonThenReturnResponseNotFoundPerson() throws Exception {
        //Given
        final UriInfo uriInfo = mock(UriInfo.class);
        final String personId = "id";
        final PersonPayload personPayload = new PersonPayload("","","");
        when(personService.updatePerson(eq(personId),any())).thenReturn(Optional.empty());

        //When
        final PersonNotFoundException personNotFoundException = Assertions.assertThrows(PersonNotFoundException.class, () -> this.personResource.updatePerson(personId, personPayload, uriInfo));


        //Then
        assertThat(personNotFoundException,is(notNullValue()));
        assertThat(personNotFoundException.getStatus(),is(Status.NOT_FOUND));
        assertThat(personNotFoundException.getTitle(),is("Person is not found"));
        assertThat(personNotFoundException.getDetail(),is("Person with identifier '" + personId + "' is not found"));
        assertThat(personNotFoundException.getCause(),is(nullValue()));
        assertThat(personNotFoundException.getParameters(),is(Map.of("personId",personId)));
    }

    @Test
    public void givenAPersonPayloadAndPersonIdWhenInvokeDeletePersonThenReturnResponse() throws Exception {
        //Given
        final UriInfo uriInfo = mock(UriInfo.class);
        final UriBuilder uriBuilder = new JerseyUriBuilder().path("id");
        final String personId = "id";
        when(personService.deletePerson(eq(personId))).thenReturn(Optional.of(personId));
        when(uriInfo.getRequestUriBuilder()).thenReturn(uriBuilder);

        //When
        final Response result = this.personResource.deletePerson(personId,uriInfo);

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        assertThat(result.getLocation(), is(notNullValue()));
        assertThat(result.getLocation(), is(new JerseyUriBuilder().path(personId).build()));
    }

    @Test
    public void givenAPersonPayloadAndPersonIdWhenInvokeDeletePersonThenReturnResponseNotFoundPerson() throws Exception {
        //Given
        final UriInfo uriInfo = mock(UriInfo.class);
        final String personId = "id";
        when(personService.deletePerson(eq(personId))).thenReturn(Optional.empty());

        //When
        final PersonNotFoundException personNotFoundException = Assertions.assertThrows(PersonNotFoundException.class, () -> this.personResource.deletePerson(personId, uriInfo));


        //Then
        assertThat(personNotFoundException,is(notNullValue()));
        assertThat(personNotFoundException.getStatus(),is(Status.NOT_FOUND));
        assertThat(personNotFoundException.getTitle(),is("Person is not found"));
        assertThat(personNotFoundException.getDetail(),is("Person with identifier '" + personId + "' is not found"));
        assertThat(personNotFoundException.getCause(),is(nullValue()));
        assertThat(personNotFoundException.getParameters(),is(Map.of("personId",personId)));
    }

}