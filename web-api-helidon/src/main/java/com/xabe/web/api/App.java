package com.xabe.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.xabe.web.api.domain.service.PersonService;
import com.xabe.web.api.domain.service.PersonServiceImpl;
import com.xabe.web.api.resource.PersonResource;
import io.helidon.common.http.Http;
import io.helidon.config.Config;
import io.helidon.health.HealthSupport;
import io.helidon.health.checks.HealthChecks;
import io.helidon.media.jackson.server.JacksonSupport;
import io.helidon.metrics.MetricsSupport;
import io.helidon.webserver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.zalando.problem.*;

import javax.validation.*;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.stream.Collectors;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    public static final String APPLICATION_PROBLEM_JSON = "application/problem+json";
    private static WebServer server;

    public static void main(final String[] args) throws IOException {
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        java.util.logging.Logger.getLogger("global").setLevel(Level.ALL);
        final Config config = Config.create();
        final ServerConfiguration serverConfig = ServerConfiguration.create(config.get("server"));
        App.server = WebServer.create(serverConfig, createRouting(config));
        // Try to start the server. If successful, print some info and arrange to
        // print a message at shutdown. If unsuccessful, print the exception.
        App.server.start()
                .thenAccept(ws -> {
                    LOGGER.info("WEB server is up! {}", String.format("http://localhost:%d/api", ws.port()));
                    ws.whenShutdown().thenRun(() -> LOGGER.info("WEB server is DOWN. Good bye!"));
                })
                .exceptionally(t -> {
                    LOGGER.error("Startup failed: {}", t.getMessage());
                    LOGGER.error("Error: {}",t);
                    return null;
                });
        Runtime.getRuntime().addShutdownHook(new Thread(App.server::shutdown));
    }

    private static Routing createRouting(Config config) {
        final ObjectMapper mapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .registerModule(new ProblemModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        final JacksonSupport jacksonSupport = JacksonSupport.create((serverRequest, serverResponse) -> mapper);
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        final PersonService personService = new PersonServiceImpl();
        final Service resource = new PersonResource(validator, personService);
        final MetricsSupport metrics = MetricsSupport.create();
        final HealthSupport health = HealthSupport.builder()
                .addLiveness(HealthChecks.healthChecks())   // Adds a convenient set of checks
                .build();

        return Routing.builder()
                .register(jacksonSupport)
                .register(health)
                .register(metrics)
                .error(AbstractThrowableProblem.class, App::problem)
                .error(ValidationException.class, App::validation)
                .register("/api", resource)
                .build();
    }

    private static void validation(ServerRequest serverRequest, ServerResponse serverResponse, ValidationException validation) {
        if (validation instanceof ConstraintViolationException) {
            final ConstraintViolationException constraint = (ConstraintViolationException) validation;
            final ThrowableProblem problem = Problem
                    .builder()
                    .withType(URI.create("http://localhost:21020/problems/invalid-parameters"))
                    .withTitle("One or more request parameters are not valid")
                    .withStatus(Status.BAD_REQUEST)
                    .withInstance(serverRequest.uri())
                    .with("invalid-parameters", constraint
                            .getConstraintViolations()
                            .stream()
                            .map(App::buildViolation)
                            .collect(Collectors.toList()))
                    .build();
            serverResponse.status(Http.Status.BAD_REQUEST_400).headers().putIfAbsent(Http.Header.CONTENT_TYPE, APPLICATION_PROBLEM_JSON);
            serverResponse.send(problem);
        } else {
            final ThrowableProblem problem = Problem
                    .builder()
                    .withTitle("The server is not able to process the request")
                    .withType(URI.create("http://localhost:21020/problems/server-error"))
                    .withInstance(serverRequest.uri())
                    .withStatus(Status.INTERNAL_SERVER_ERROR)
                    .withDetail(validation.getMessage())
                    .build();
            serverResponse.status(Http.Status.INTERNAL_SERVER_ERROR_500).headers().putIfAbsent(Http.Header.CONTENT_TYPE, APPLICATION_PROBLEM_JSON);
            serverResponse.send(problem);
        }
    }

    private static Map<?, ?> buildViolation(ConstraintViolation<?> violation) {
        return Map.of(
                "bean", violation.getRootBeanClass().getName(),
                "property", violation.getPropertyPath().toString(),
                "reason", violation.getMessage(),
                "value", Objects.requireNonNullElse(violation.getInvalidValue(), "null")
        );
    }

    private static void problem(ServerRequest serverRequest, ServerResponse serverResponse, AbstractThrowableProblem problem) {
        serverResponse.status(problem.getStatus().getStatusCode()).headers().put(Http.Header.CONTENT_TYPE, APPLICATION_PROBLEM_JSON);
        serverResponse.send(problem);
    }
}
