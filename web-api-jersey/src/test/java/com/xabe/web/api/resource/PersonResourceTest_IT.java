package com.xabe.web.api.resource;

import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class PersonResourceTest_IT {

    private HttpClient httpClient;

    public PersonResourceTest_IT() {
        this.httpClient = createHttpClient();
    }


    @Test
    public void shouldGetOptionsPerson() throws Exception {
        //Given
        final var request = createHttpRequestOptions("http://localhost:8008/api/v1/persons/");

        //When
        final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        //Then
        assertThat(response.statusCode(), is(HttpStatus.OK_200.getStatusCode()));
        assertThat(response.headers().map().get(HttpHeaders.ALLOW), is(notNullValue()));
    }

    @Test
    public void givenAPersonIdWhenInvokeGetPersonThenReturnNotFound() throws Exception {
        //Given
        final String personId = "11111";
        final var request = createHttpRequestGet(String.format("http://localhost:8008/api/v1/persons/%s",personId));

        //When
        final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        final String result = response.body();

        //Then
        assertThat(response.statusCode(), is(HttpStatus.NOT_FOUND_404.getStatusCode()));
        assertThat(result, is(notNullValue()));
    }

    @Test
    public void givenAPersonIdNullWhenInvokeCreatePersonThenReturnBadRequest() throws Exception {
        //Given
        final var request = createHttpRequestPost("http://localhost:8008/api/v1/persons/","{\"name\":\"chabir\", \"surname\":\"atrahouch\"}");

        //When
        final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        final String result = response.body();

        //Then
        assertThat(response.statusCode(), is(HttpStatus.BAD_REQUEST_400.getStatusCode()));
        assertThat(result, is(notNullValue()));
    }

    @Test
    public void givenAPersonIdWhenInvokePatchPersonThenReturnNotFound() throws Exception {
        //Given
        final String personId = "11111";
        final var request = createHttpRequestPatch(String.format("http://localhost:8008/api/v1/persons/%s",personId), "{\"name\":\"chabir\", \"surname\":\"atrahouch\"}");

        //When
        final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        final String result = response.body();

        //Then
        assertThat(response.statusCode(), is(HttpStatus.NOT_FOUND_404.getStatusCode()));
        assertThat(result, is(notNullValue()));
    }

    @Test
    public void givenAPersonIdWhenInvokePutPersonThenReturnNotFound() throws Exception {
        //Given
        final String personId = "11111";
        final var request = createHttpRequestPut(String.format("http://localhost:8008/api/v1/persons/%s",personId), "{\"name\":\"chabir\", \"surname\":\"atrahouch\"}");

        //When
        final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        final String result = response.body();

        //Then
        assertThat(response.statusCode(), is(HttpStatus.NOT_FOUND_404.getStatusCode()));
        assertThat(result, is(notNullValue()));
    }

    @Test
    public void givenAPersonIdWhenInvokeDeletePersonThenReturnNotFound() throws Exception {
        //Given
        final String personId = "11111";
        final var request = createHttpRequestDelete(String.format("http://localhost:8008/api/v1/persons/%s",personId));

        //When
        final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        final String result = response.body();

        //Then
        assertThat(response.statusCode(), is(HttpStatus.NOT_FOUND_404.getStatusCode()));
        assertThat(result, is(notNullValue()));
    }

    private HttpRequest createHttpRequestOptions(String uri) {
        return HttpRequest
                .newBuilder(URI.create(uri))
                .timeout(Duration.ofMillis(5000))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .build();
    }

    private HttpRequest createHttpRequestGet(String uri) {
        return HttpRequest
                .newBuilder(URI.create(uri))
                .timeout(Duration.ofMillis(5000))
                .GET()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .build();
    }

    private HttpRequest createHttpRequestPost(String uri, String body) {
        return HttpRequest
                .newBuilder(URI.create(uri))
                .timeout(Duration.ofMillis(5000))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .build();
    }

    private HttpRequest createHttpRequestPatch(String uri, String body) {
        return HttpRequest
                .newBuilder(URI.create(uri))
                .timeout(Duration.ofMillis(5000))
                .method("PATCH",HttpRequest.BodyPublishers.ofString(body))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .build();
    }

    private HttpRequest createHttpRequestPut(String uri, String body) {
        return HttpRequest
                .newBuilder(URI.create(uri))
                .timeout(Duration.ofMillis(5000))
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .build();
    }

    private HttpRequest createHttpRequestDelete(String uri) {
        return HttpRequest
                .newBuilder(URI.create(uri))
                .timeout(Duration.ofMillis(5000))
                .DELETE()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .build();
    }

    private HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)  // this is the default
                .connectTimeout(Duration.ofMillis(5000))
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }

}