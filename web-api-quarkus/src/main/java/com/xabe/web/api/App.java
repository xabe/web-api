package com.xabe.web.api;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusMain
public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(final String... args) {
        LOGGER.info("Running main method");
        Quarkus.run(args);
    }
}
