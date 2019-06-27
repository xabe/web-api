package com.xabe.web.api.domain.model;

public class PersonCloneBuilder extends PersonBuilder {

    PersonCloneBuilder(String personId) {
        super(personId);
    }

    public static PersonCloneBuilder clone(Person person) {
        final PersonCloneBuilder builder = new PersonCloneBuilder(person.getPersonId());
        builder.surname = person.getSurname().orElse("");
        builder.name = person.getName().orElse("");
        return builder;
    }

    public PersonBuilder withPersonId(String personId){
        this.personId = personId;
        return this;
    }
}
