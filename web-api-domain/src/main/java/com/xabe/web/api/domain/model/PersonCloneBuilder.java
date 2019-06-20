package com.xabe.web.api.domain.model;

public class PersonCloneBuilder extends PersonBuilder {

    PersonCloneBuilder(String personId) {
        super(personId);
    }

    public static PersonCloneBuilder clone(Person person) {
        final PersonCloneBuilder builder = new PersonCloneBuilder(person.getPersonId());
        builder.surname = person.getSurname().get();
        builder.name = person.getName().get();
        return builder;
    }

    public PersonBuilder withPersonId(String personId){
        this.personId = personId;
        return this;
    }
}
