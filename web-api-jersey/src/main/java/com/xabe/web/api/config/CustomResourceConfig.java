package com.xabe.web.api.config;

import com.xabe.web.api.config.jackson.ObjectMapperContextResolver;
import com.xabe.web.api.domain.service.PersonService;
import com.xabe.web.api.domain.service.PersonServiceImpl;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("")
public class CustomResourceConfig extends ResourceConfig {

    public CustomResourceConfig() {
        packages("com.xabe.web.api.resource");
        packages("com.xabe.web.api.exception");
        register(JacksonFeature.class);
        register(new ObjectMapperContextResolver());
        register(new LoggingFeature());
        property( ServerProperties.BV_FEATURE_DISABLE, false );
        property( ServerProperties.RESOURCE_VALIDATION_IGNORE_ERRORS, true );
        property( ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);

        //property( ServerProperties.TRACING, "ALL" );
        //property( ServerProperties.TRACING_THRESHOLD, "VERBOSE" );
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(PersonServiceImpl.class).to(PersonService.class).in(Singleton.class);
            }
        });
    }
}

