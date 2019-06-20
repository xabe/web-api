package com.xabe.web.api.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xabe.web.api.domain.model.Person;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public class PersonPayload {

    public String name;
    public String surname;
    @NotNull
    public String personId;

    public PersonPayload(Person person) {
        this.name = person.getName().get();
        this.surname = person.getSurname().get();
        this.personId = person.getPersonId();
    }

    @JsonCreator
    public PersonPayload(
            @JsonProperty("name") String name,
            @JsonProperty("surname") String surname,
            @JsonProperty("personId") String personId) {
        this.name = name;
        this.surname = surname;
        this.personId = personId;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getSurname() {
        return Optional.ofNullable(surname);
    }

    public String getPersonId() {
        return personId;
    }
}
