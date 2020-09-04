package com.xabe.web.api.resource;

import com.xabe.web.api.domain.model.Person;
import com.xabe.web.api.domain.service.PersonService;
import com.xabe.web.api.exception.PersonNotFoundException;
import com.xabe.web.api.payload.PersonPayload;
import io.helidon.common.http.Http;
import io.helidon.common.http.HttpRequest;
import io.helidon.common.http.MediaType;
import io.helidon.common.reactive.Single;
import io.helidon.webserver.ResponseHeaders;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.xabe.web.api.resource.PersonResource.APPLICATION_PROBLEM_JSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.*;


class PersonResourceTest {

    private static Validator validator;
    private PersonService personService;
    private Routing routing;
    private PersonResource personResource;

    @BeforeAll
    public static void setUpValidator() {
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    public void setUp() throws Exception {
        this.personService = mock(PersonService.class);
        this.personResource = new PersonResource(validator,personService);
        final Routing.Builder rules = Routing.builder();
        this.personResource.update(rules);
        this.routing = rules.build();
    }

    @Test
    public void shouldHaveAllRules() throws Exception {
        MatcherAssert.assertThat(routing,is(notNullValue()));
    }

    @Test
    public void shouldReturnResponseWithOptionsPerson() throws Exception {
        //Given
        final ServerRequest serverRequest = mock(ServerRequest.class);
        final ServerResponse serverResponse = mock(ServerResponse.class);
        final ResponseHeaders responseHeaders = mock(ResponseHeaders.class);
        when(serverResponse.headers()).thenReturn(responseHeaders);

        //When
        personResource.getOptions(serverRequest,serverResponse);


        //Then
        verify(serverResponse).status(eq(Http.Status.OK_200));
        verify(responseHeaders).putIfAbsent(Http.Header.ALLOW,Http.Method.GET.name(), Http.Method.POST.name(), Http.Method.PUT.name(), Http.Method.DELETE.name());
        verify(responseHeaders).putIfAbsent(Http.Header.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        verify(responseHeaders).putIfAbsent(Http.Header.CONTENT_LENGTH,"0");
        verify(serverResponse).send();
    }

    @Test
    public void givenAPersonIdWhenInvokeGetPersonThenReturnPerson() throws Exception {
        //Given
        final ServerRequest serverRequest = mock(ServerRequest.class);
        final HttpRequest.Path path = mock(HttpRequest.Path.class);
        when(serverRequest.path()).thenReturn(path);
        when(path.param(eq("personId"))).thenReturn("id");
        when(personService.getPerson(any())).thenReturn(Optional.of(Person.of("","","")));
        final ServerResponse serverResponse = mock(ServerResponse.class);
        final ResponseHeaders responseHeaders = mock(ResponseHeaders.class);
        when(serverResponse.headers()).thenReturn(responseHeaders);
        when(serverResponse.status(any())).thenReturn(serverResponse);
        when(serverResponse.send(isA(PersonPayload.class))).thenReturn(CompletableFuture.completedStage(serverResponse));

        //When
        personResource.getPersons(serverRequest,serverResponse);


        //Then
        verify(serverResponse).status(eq(Http.Status.OK_200));
        verify(responseHeaders).putIfAbsent(Http.Header.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
    }

    @Test
    public void givenAPersonIdWhenInvokeGetPersonThenReturnPersonNotFoundException() throws Exception {
        //Given
        final String personId = "id";
        final ServerRequest serverRequest = mock(ServerRequest.class);
        final HttpRequest.Path path = mock(HttpRequest.Path.class);
        when(serverRequest.path()).thenReturn(path);
        when(path.param(eq("personId"))).thenReturn(personId);
        when(serverRequest.uri()).thenReturn(URI.create("http://mock:8080/mock"));
        when(personService.getPerson(any())).thenReturn(Optional.empty());
        final ServerResponse serverResponse = mock(ServerResponse.class);

        //When
        final PersonNotFoundException result = Assertions.assertThrows(PersonNotFoundException.class, () -> personResource.getPersons(serverRequest, serverResponse));


        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result,is(notNullValue()));
        assertThat(result.getStatus(),is(Status.NOT_FOUND));
        assertThat(result.getTitle(),is("Person is not found"));
        assertThat(result.getDetail(),is("Person with identifier '" + personId + "' is not found"));
        assertThat(result.getCause(),is(nullValue()));
        assertThat(result.getParameters(),is(Map.of("personId",personId)));
    }

    @Test
    public void givenAPersonPayloadWhenInvokeCreatePersonThenReturnConstraintViolationException() throws Exception {
        //Given
        final PersonPayload personPayload = new PersonPayload("","",null);
        final ServerRequest serverRequest = mock(ServerRequest.class);
        final ServerResponse serverResponse = mock(ServerResponse.class);


        //When
        final ConstraintViolationException result = Assertions.assertThrows(ConstraintViolationException.class, () -> personResource.createPersons(serverRequest, serverResponse, personPayload));


        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.getConstraintViolations(),is(notNullValue()));
        assertThat(result.getConstraintViolations().size(),is(greaterThanOrEqualTo(1)));
    }

    @Test
    public void givenAPersonPayloadWhenInvokeCreatePersonThenReturnProblem() throws Exception {
        //Given
        final PersonPayload personPayload = new PersonPayload("","","1");
        when(personService.createPerson(any())).thenReturn(Optional.empty());
        final ServerRequest serverRequest = mock(ServerRequest.class);
        final ServerResponse serverResponse = mock(ServerResponse.class);
        final ResponseHeaders responseHeaders = mock(ResponseHeaders.class);
        when(serverResponse.headers()).thenReturn(responseHeaders);


        //When
        personResource.createPersons(serverRequest, serverResponse, personPayload);


        //Then
        verify(serverResponse).status(eq(Http.Status.BAD_REQUEST_400));
        verify(responseHeaders).putIfAbsent(Http.Header.CONTENT_TYPE, APPLICATION_PROBLEM_JSON);
        final ArgumentCaptor<ThrowableProblem> captor = ArgumentCaptor.forClass(ThrowableProblem.class);
        verify(serverResponse).send(captor.capture());
        final ThrowableProblem problem = captor.getValue();
        assertThat(problem, is(notNullValue()));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getTitle(),is("The person is not unique"));
        assertThat(problem.getDetail(),is("Error create person"));
        assertThat(problem.getCause(),is(nullValue()));
        assertThat(problem.getParameters(),is(Map.of("personId","1")));
    }

    @Test
    public void givenAPersonPayloadWhenInvokeCreatePersonThenOk() throws Exception {
        //Given
        final PersonPayload personPayload = new PersonPayload("","","1");
        when(personService.createPerson(any())).thenReturn(Optional.of("1"));
        final ServerRequest serverRequest = mock(ServerRequest.class);
        final HttpRequest.Path path = mock(HttpRequest.Path.class);
        when(serverRequest.path()).thenReturn(path);
        when(path.param(any())).thenReturn("path");
        final ServerResponse serverResponse = mock(ServerResponse.class);
        final ResponseHeaders responseHeaders = mock(ResponseHeaders.class);
        when(serverResponse.headers()).thenReturn(responseHeaders);


        //When
        personResource.createPersons(serverRequest, serverResponse, personPayload);


        //Then
        verify(serverResponse).status(eq(Http.Status.CREATED_201));
        verify(responseHeaders).putIfAbsent(Http.Header.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        verify(responseHeaders).putIfAbsent(Http.Header.LOCATION, "path");
    }


}