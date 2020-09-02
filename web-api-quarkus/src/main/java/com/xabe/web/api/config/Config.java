package com.xabe.web.api.config;

import com.xabe.web.api.domain.service.PersonService;
import com.xabe.web.api.domain.service.PersonServiceImpl;
import io.quarkus.arc.DefaultBean;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
public class Config {

    @Produces
    @DefaultBean
    public PersonService personService(){
        return new PersonServiceImpl();
    }
}
