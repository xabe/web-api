package com.xabe.web.api.domain.service;


import com.xabe.web.api.domain.model.Person;
import com.xabe.web.api.domain.model.PersonBuilder;
import com.xabe.web.api.domain.model.PersonCloneBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

public class PersonServiceImpl implements PersonService {

    private final Map<String, Person> personMap;

    public PersonServiceImpl() {
        personMap = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<Person> getPerson(String personId) {
        return Optional.ofNullable(personMap.get(personId));
    }

    @Override
    public Optional<String> createPerson(Person person) {
        if(Objects.nonNull(personMap.get(person.getPersonId()))){
            return Optional.empty();
        }
        else{
            personMap.putIfAbsent(person.getPersonId(),person);
            return Optional.of(person.getPersonId());
        }
    }

    @Override
    public Optional<String> patchPerson(String personId, Person personUpdate) {
        final Person person = personMap.get(personId);
        if(Objects.isNull(person)){
            return Optional.empty();
        }
        else{
            final List<UnaryOperator<PersonBuilder>> operators = List.of(updateName(personUpdate), updateSurname(personUpdate));
            final PersonBuilder builder = PersonCloneBuilder.clone(person).withPersonId(personId);
            final PersonBuilder personBuilder = operators.stream().reduce(
                    builder,
                    (p, u) -> u.apply(p),
                    (a, b) -> a);
            this.personMap.put(personId, personBuilder.build());
            return Optional.of(personId);
        }
    }

    @Override
    public Optional<String> updatePerson(String personId, Person personUpdate) {
        final Person person = personMap.get(personId);
        if(Objects.isNull(person)){
            return Optional.empty();
        }
        else{
            final List<UnaryOperator<PersonBuilder>> operators = List.of(updateName(personUpdate), updateSurname(personUpdate));
            final PersonBuilder builder = Person.builder(personId);
            final PersonBuilder personBuilder = operators.stream().reduce(
                    builder,
                    (p, u) -> u.apply(p),
                    (a, b) -> a);
            this.personMap.put(personId, personBuilder.build());
            return Optional.of(personId);
        }
    }

    @Override
    public Optional<String> deletePerson(String personId) {
        final Person person = personMap.get(personId);
        if(Objects.isNull(person)){
            return Optional.empty();
        }
        else{
            this.personMap.remove(personId);
            return Optional.of(personId);
        }
    }

    private UnaryOperator<PersonBuilder> updateName(Person personPayload) {
        return personBuilder -> {
            personPayload.getName().ifPresent( name -> personBuilder.withName(name));
            return personBuilder;
        };
    }

    private UnaryOperator<PersonBuilder> updateSurname(Person personPayload) {
        return personBuilder -> {
            personPayload.getSurname().ifPresent( name -> personBuilder.withSurname(name));
            return personBuilder;
        };
    }
}
