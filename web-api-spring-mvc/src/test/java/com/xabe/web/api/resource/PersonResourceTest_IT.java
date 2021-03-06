package com.xabe.web.api.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class PersonResourceTest_IT {

    private HttpEntity<String> httpEntity;
    private TestRestTemplate restTemplate;


    @BeforeEach
    public void setUp() throws Exception {
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        this.httpEntity = new HttpEntity<>(headers);
        this.restTemplate = new TestRestTemplate();
    }

    @Test
    public void shouldGetOptionsPerson() throws Exception {
        //Given

        //When
        final ResponseEntity<String> result = this.restTemplate.exchange("http://localhost:8008/api/v1/persons/", HttpMethod.OPTIONS, httpEntity, String.class);

        //Then
        assertThat(result.getStatusCode().value(), is(HttpStatus.OK.value()));
        assertThat(result.getHeaders().get(HttpHeaders.ALLOW), is(notNullValue()));
    }


    @Test
    public void givenAPersonIdWhenInvokeGetPersonThenReturnPersonNotFound() throws Exception {
        //Given
        final String personId = "11111";

        //When
        final ResponseEntity<String> result = this.restTemplate.exchange(String.format("http://localhost:8008/api/v1/persons/%s",personId), HttpMethod.GET, httpEntity, String.class);

        //Then
        assertThat(result.getStatusCode().value(), is(HttpStatus.NOT_FOUND.value()));
        assertThat(result.getBody(), is(notNullValue()));
    }

    @Test
    public void givenAPersonIdNullWhenInvokeCreatePersonThenReturnBadRequest() throws Exception {
        //Given
        final String body = "{\"name\":\"chabir\", \"surname\":\"atrahouch\"}";

        //When
        final var response = this.restTemplate.exchange("http://localhost:8008/api/v1/persons/",HttpMethod.POST, new HttpEntity<>(body,httpEntity.getHeaders()),String.class);
        final String result = response.getBody();

        //Then
        assertThat(response.getStatusCodeValue(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(result, is(notNullValue()));
    }
}
