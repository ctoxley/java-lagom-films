package com.tentoltd.films.inventory.entity;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

import java.util.Optional;

public interface PFilmCommand extends Jsonable {

    enum GetFilm implements PFilmCommand, PersistentEntity.ReplyType<Optional<PFilm>> {
        INSTANCE
    }

    @Value
    final class CreateFilm implements PFilmCommand, PersistentEntity.ReplyType<Done> {

        PFilm film;

        @JsonCreator
        public CreateFilm(PFilm film) { this.film = film; }
    }
}
