package com.xabe.web.api.domain.service;

import com.xabe.web.api.domain.model.Person;

import java.util.Optional;

public interface PersonService {
    Optional<Person> getPerson(String personId);
    Optional<String> createPerson(Person person);
    Optional<String> patchPerson(String personId, Person person);
    Optional<String> updatePerson(String personId, Person person);
    Optional<String> deletePerson(String personId);
}
