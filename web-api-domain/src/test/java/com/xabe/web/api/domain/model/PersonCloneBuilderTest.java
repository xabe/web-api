package com.xabe.web.api.domain.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class PersonCloneBuilderTest {


    @Test
    public void shoudlClonePerson() throws Exception {
        //Given
        final Person person = new Person("pepe","lopez","id");

        //When
        final Person result = PersonCloneBuilder.clone(person).withPersonId("clone").build();

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.getName().get(), is("pepe"));
        assertThat(result.getSurname().get(), is("lopez"));
        assertThat(result.getPersonId(), is("clone"));
    }

    @Test
    public void shoudlClonePersonEmpty() throws Exception {
        //Given
        final Person person = new Person(null,null,"id");

        //When
        final Person result = PersonCloneBuilder.clone(person).withPersonId("clone").build();

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.getName().get(), is(""));
        assertThat(result.getSurname().get(), is(""));
        assertThat(result.getPersonId(), is("clone"));
    }

}