package com.xabe.web.api.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xabe.web.api.domain.model.Person;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.Optional;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PersonPayload {

    public String name;
    public String surname;
    @NotEmpty
    public String personId;

    @JsonCreator
    public static PersonPayload of(
            @JsonProperty("name") String name,
            @JsonProperty("surname") String surname,
            @JsonProperty("personId") String personId) {
        return PersonPayload.builder().name(name).personId(personId).surname(surname).build();
    }

    public static PersonPayload of(Person person) {
        final PersonPayloadBuilder builder = PersonPayload.builder();
        builder.personId(person.getPersonId());
        person.getName().ifPresent(builder::name);
        person.getSurname().ifPresent(builder::surname);
        return builder.build();
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

    public Person toPerson() {
        return Person.of(this.name,this.surname,this.personId);
    }
}
