package com.xabe.web.api.exception;

import com.xabe.web.api.config.MediaTypeExt;
import com.xabe.web.api.payload.PersonPayload;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.ValidationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ValidationExceptionMapperTest {

    private UriInfo uriInfo;
    private ExceptionMapper exceptionMapper;
    
    @BeforeEach
    public void setUp() throws Exception {
        this.uriInfo = mock(UriInfo.class);
        this.exceptionMapper = new ValidationExceptionMapper(uriInfo);
    }

    @Test
    public void shouldHaveEmptyConstructor() throws Exception {
        assertThat(new ValidationExceptionMapper(), is(notNullValue()));
    }
    
    @Test
    public void givenAValidationExceptionWhenInvokeToResponseThenReturnResponse() throws Exception {
        //Given
        final ValidationException validationException = new ValidationException("error");
        final URI uri = URI.create("http://localhost:9000/api/persons/");
        when(uriInfo.getRequestUri()).thenReturn(uri);

        //When
        final Response result = this.exceptionMapper.toResponse(validationException);

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.getStatus(), is(HttpStatus.INTERNAL_SERVER_ERROR_500.getStatusCode()));
        assertThat(result.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0), is(MediaTypeExt.APPLICATION_PROBLEM_JSON_TYPE));
        final ThrowableProblem problem = ThrowableProblem.class.cast(result.getEntity());
        assertThat(problem, is(notNullValue()));
        assertThat(problem.getTitle(), is("The server is not able to process the request"));
        assertThat(problem.getType(), is(notNullValue()));
        assertThat(problem.getInstance(), is(uri));
        assertThat(problem.getStatus(), is(Status.INTERNAL_SERVER_ERROR));
        assertThat(problem.getDetail(), is("error"));
    }

    @Test
    public void givenAConstraintViolationExceptionWhenInvokeToResponseThenReturnResponse() throws Exception {
        //Given
        final Path path = PathImpl.createPathFromString("personId");
        final ConstraintViolation<?> constraintViolation = ConstraintViolationImpl.forBeanValidation("error", Map.of(),Map.of(),"interpolatedMessage", PersonPayload.class,new PersonPayload("","",""),null,null, path
                ,null,null);
        final ValidationException validationException = new ConstraintViolationException(Set.of(constraintViolation));
        final URI uri = URI.create("http://localhost:9000/api/persons/");
        when(uriInfo.getRequestUri()).thenReturn(uri);

        //When
        final Response result = this.exceptionMapper.toResponse(validationException);

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.getStatus(), is(HttpStatus.BAD_REQUEST_400.getStatusCode()));
        assertThat(result.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0), is(MediaTypeExt.APPLICATION_PROBLEM_JSON_TYPE));
        final ThrowableProblem problem = ThrowableProblem.class.cast(result.getEntity());
        assertThat(problem, is(notNullValue()));
        assertThat(problem.getTitle(), is("One or more request parameters are not valid"));
        assertThat(problem.getType(), is(notNullValue()));
        assertThat(problem.getInstance(), is(uri));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getParameters(), is(notNullValue()));
        assertThat(problem.getParameters().get("invalid-parameters"), is(notNullValue()));
    }
    
}