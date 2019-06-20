package com.xabe.web.api.domain.model;

import java.io.Serializable;
import java.util.Optional;

public class Person implements Serializable {
    private final String name;
    private final String surname;
    private final String personId;

    Person(String name, String surname, String personId) {
        this.name = name;
        this.surname = surname;
        this.personId = personId;
    }

    public static PersonBuilder builder(String personId) {
        return new PersonBuilder(personId);
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

    public static Person of(String name, String surname, String personId) {
        return new Person(name, surname, personId);
    }
}
