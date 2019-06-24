package com.xabe.web.api.resource;

import io.helidon.common.http.Http;
import io.helidon.common.http.MediaType;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class PersonResourceTest_IT {


    @BeforeAll
    public static void setUp() throws Exception {
        Unirest.config().connectTimeout(1000).socketTimeout(1000).addDefaultHeader(Http.Header.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
    }

    @Test
    public void shouldGetOptionsPerson() throws Exception {
        //Given

        //When
        final HttpResponse result = Unirest.options("http://localhost:8008/api/v1/persons/").asEmpty();

        //Then
        assertThat(result.getStatus(), is(Http.Status.OK_200.code()));
        assertThat(result.getHeaders().get(HttpHeaders.ALLOW), is(notNullValue()));
    }


    @Test
    public void givenAPersonIdWhenInvokeGetPersonThenReturnPersonNotFound() throws Exception {
        //Given
        final String personId = "11111";

        //When
        final HttpResponse<JsonNode> result = Unirest.get(String.format("http://localhost:8008/api/v1/persons/%s",personId)).asJson();

        //Then
        assertThat(result.getStatus(), is(Http.Status.NOT_FOUND_404.code()));
        assertThat(result.getBody(), is(notNullValue()));
    }

    @Test
    public void givenAPersonIdNullWhenInvokeCreatePersonThenReturnBadRequest() throws Exception {
        //Given
        final String body = "{\"name\":\"chabir\", \"surname\":\"atrahouch\"}";

        //When
        final var response = Unirest.post("http://localhost:8008/api/v1/persons/").body(body).asJson();
        final JsonNode result = response.getBody();

        //Then
        assertThat(response.getStatus(), is(Http.Status.BAD_REQUEST_400.code()));
        assertThat(result, is(notNullValue()));
    }
}
