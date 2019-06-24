package com.xabe.web.api.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr353.JSR353Module;
import com.xabe.web.api.domain.service.PersonService;
import com.xabe.web.api.domain.service.PersonServiceImpl;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import java.util.List;

@Configuration
@ComponentScan(basePackages={ "com.xabe.web.api.resource"})
@EnableAutoConfiguration(exclude = ErrorMvcAutoConfiguration.class)
public class WebMVCConfig {

    @Configuration
    public static class CustomWebMvcConfig extends WebMvcConfigurerAdapter {

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**");
        }

        @SuppressWarnings("unchecked")
        @Bean
        public Jackson2ObjectMapperBuilder jacksonBuilder() {
            final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            return builder.indentOutput(true).simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .featuresToEnable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                    .modulesToInstall(new ProblemModule(), new ConstraintViolationProblemModule())
                    .modulesToInstall(new Class[]{JavaTimeModule.class, Jdk8Module.class, JSR353Module.class});
        }

        @Bean
        public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter(ObjectMapper objectMapper) {
            final MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
            jsonConverter.setObjectMapper(objectMapper);
            return jsonConverter;
        }

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(new StringHttpMessageConverter());
            converters.add(customJackson2HttpMessageConverter(jacksonBuilder().build()));
            super.configureMessageConverters(converters);
        }

        @Override
        public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
            configurer.defaultContentType(MediaType.APPLICATION_JSON);
        }
    }

    @Bean(name = "messageSource")
    public MessageSource getMessageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(true);
        messageSource.setCacheSeconds(-1);
        return messageSource;
    }

    @Bean
    protected Validator getValidator() {
        final LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setValidationMessageSource(getMessageSource());
        return factory;
    }

    @Bean
    public PersonService personService(){
        return new PersonServiceImpl();
    }

}
