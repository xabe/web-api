package com.xabe.web.api.domain.model;

public class PersonBuilder {

    protected String personId;
    protected String name;
    protected String surname;

    PersonBuilder(String personId){
        this.personId = personId;
    }

    public PersonBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public PersonBuilder withSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public Person build() {
        return new Person(name,surname,personId);
    }


}
