package com.xabe.web.api.resource;

import com.xabe.web.api.domain.model.Person;
import com.xabe.web.api.domain.service.PersonService;
import com.xabe.web.api.exception.PersonNotFoundException;
import com.xabe.web.api.payload.PersonPayload;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.zalando.problem.Status;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
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
        final ResponseEntity result = this.personResource.optionsForPersonResource();

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.getHeaders().getAllow(), is(Set.of(HttpMethod.GET, HttpMethod.POST,HttpMethod.PATCH,HttpMethod.PUT, HttpMethod.DELETE)));

    }

    @Test
    public void givenAPersonIdWhenInvokeGetPersonThenReturnPersonPayload() throws Exception {
        //Given
        final String personId = "id";
        when(personService.getPerson(eq(personId))).thenReturn(Optional.of(Person.of("","","")));

        //When
        final PersonPayload result = this.personResource.getPerson(personId);

        //Then
        assertThat(result, is(notNullValue()));
    }

    @Test
    public void givenAPersonIdWhenInvokeGetPersonThenReturnPersonNotFound() throws Exception {
        //Given
        final String personId = "id";
        when(personService.getPerson(any())).thenReturn(Optional.empty());
        final MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.setScheme("http");
        httpServletRequest.setServerName("localhost");
        httpServletRequest.setServerPort(-1);
        httpServletRequest.setRequestURI("/mvc");
        httpServletRequest.setContextPath("/mvc");
        ServletUriComponentsBuilder.fromServletMapping(httpServletRequest);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));


        //When
        final PersonNotFoundException personNotFoundException = Assertions.assertThrows(PersonNotFoundException.class, () -> this.personResource.getPerson(personId));

        //Then
        assertThat(personNotFoundException, is(notNullValue()));
        assertThat(personNotFoundException.getStatus(),is(Status.NOT_FOUND));
        assertThat(personNotFoundException.getTitle(),is("Person is not found"));
        assertThat(personNotFoundException.getDetail(),is("Person with identifier '" + personId + "' is not found"));
        assertThat(personNotFoundException.getCause(),is(nullValue()));
        assertThat(personNotFoundException.getParameters(),is(Map.of("id",personId)));

    }


}