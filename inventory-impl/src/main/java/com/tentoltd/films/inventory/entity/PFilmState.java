package com.tentoltd.films.inventory.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

import java.util.Optional;

import static com.tentoltd.films.inventory.entity.PFilm.Status.NOT_CREATED;

@Value
public class PFilmState implements Jsonable {

    Optional<PFilm> optionalFilm;

    @JsonCreator
    public PFilmState(Optional<PFilm> film) {
        this.optionalFilm = film;
    }

    public static PFilmState empty() {
        return new PFilmState(Optional.empty());
    }

    public static PFilmState create(PFilm film) {
        return new PFilmState(Optional.of(film));
    }

    public PFilm.Status getStatus() {
        return optionalFilm.map(PFilm::getStatus).orElse(NOT_CREATED);
    }
}
