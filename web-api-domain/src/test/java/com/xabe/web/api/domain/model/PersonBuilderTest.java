package com.xabe.web.api.domain.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class PersonBuilderTest {

    @Test
    public void shouldCreatePerson() throws Exception {
        final Person result = Person.builder("id").withName("name").withSurname("surname").build();

        assertThat(result, is(notNullValue()));
    }

}