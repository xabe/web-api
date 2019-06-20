package com.xabe.web.api.domain.service;

import com.xabe.web.api.domain.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class PersonServiceImplTest {

    private PersonService personService;

    @BeforeEach
    public void setUp() throws Exception {
        this.personService = new PersonServiceImpl();
    }

    @Test
    public void givenAPersonIdWhenInvokeGetPersonThenReturnOptionalEmpty() throws Exception {
        //Given
        final String personId = "idGet";

        //When
        final Optional<Person> result = this.personService.getPerson(personId);

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.isEmpty(), is(true));

    }

    @Test
    public void givenAPersonIdWhenInvokeGetPersonThenReturnOptionalPerson() throws Exception {
        //Given
        final String personId = "idGetContent";
        final Person person =  Person.of("","",personId);
        this.personService.createPerson(person);

        //When
        final Optional<Person> result = this.personService.getPerson(personId);

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.isEmpty(), is(false));
    }

    @Test
    public void givenAPersonWhenInvokeCreatePersonThenReturnOptionalEmpty() throws Exception {
        //Given
        final String personId = "idCreate";
        final Person personPayload = Person.of("","",personId);
        this.personService.createPerson(personPayload);

        //When
        final Optional<String> result = this.personService.createPerson(personPayload);

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void givenAPersonIdAndPersonWhenInvokePatchPersonThenReturnOptionalEmpty() throws Exception {
        //Given
        final String personId = "idPatch";
        final Person personPayload =  Person.of("","",personId);

        //When
        final Optional<String> result = this.personService.patchPerson(personId, personPayload);

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void givenAPersonIdAndPersonWhenInvokePatchPersonThenReturnOptionalPersonIdAndUpdateName() throws Exception {
        //Given
        final String personId = "idPatch2";
        final Person personPayload = Person.of("name","",personId);
        this.personService.createPerson(Person.of("","",personId));

        //When
        final Optional<String> result = this.personService.patchPerson(personId, personPayload);

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.isEmpty(), is(false));
        assertThat(result.get(), is(personId));
        final Optional<Person> person = this.personService.getPerson(personId);
        assertThat(person.get().getName().get(),is("name"));
        assertThat(person.get().getSurname().get(),is(""));
    }

    @Test
    public void givenAPersonIdAndPersonWhenInvokePatchPersonThenReturnOptionalPersonIdAndUpdateSurname() throws Exception {
        //Given
        final String personId = "idPatch3";
        final Person personPayload =  Person.of("","Surname",personId);
        this.personService.createPerson(Person.of("","",personId));

        //When
        final Optional<String> result = this.personService.patchPerson(personId, personPayload);

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.isEmpty(), is(false));
        assertThat(result.get(), is(personId));
        final Optional<Person> person = this.personService.getPerson(personId);
        assertThat(person.get().getName().get(),is(""));
        assertThat(person.get().getSurname().get(),is("Surname"));
    }

    @Test
    public void givenAPersonIdAndPersonPayloadWhenInvokeUpdatePersonThenReturnOptionalEmpty() throws Exception {
        //Given
        final String personId = "idUpdate";
        final Person personPayload = Person.of("","",personId);

        //When
        final Optional<String> result = this.personService.updatePerson(personId, personPayload);

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void givenAPersonIdAndPersonWhenInvokeUpdatePersonThenReturnOptional() throws Exception {
        //Given
        final String personId = "idUpdate2";
        this.personService.createPerson(Person.of("name","",personId));

        //When
        final Optional<String> result = this.personService.updatePerson(personId, Person.of("","nuevo",""));

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.isEmpty(), is(false));
        final Optional<Person> person = this.personService.getPerson(personId);
        assertThat(person.get().getName().get(),is(""));
        assertThat(person.get().getSurname().get(),is("nuevo"));
    }

    @Test
    public void givenAPersonIdAndPersonWhenInvokeDeletePersonThenReturnOptionalEmpty() throws Exception {
        //Given
        final String personId = "idDelete";

        //When
        final Optional<String> result = this.personService.deletePerson(personId);

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void givenAPersonIdAndPersonPayloadWhenInvokeDeletePersonThenReturnOptional() throws Exception {
        //Given
        final String personId = "idDelete2";
        this.personService.createPerson(Person.of("","",personId));

        //When
        final Optional<String> result = this.personService.deletePerson(personId);

        //Then
        assertThat(result, is(notNullValue()));
        assertThat(result.isEmpty(), is(false));
        final Optional<Person> person = this.personService.getPerson(personId);
        assertThat(person.isEmpty(),is(true));
    }

}