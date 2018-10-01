package com.tentoltd.films.inventory.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

public interface PFilmEvent extends AggregateEvent<PFilmEvent>, Jsonable {

    int NUM_SHARDS = 2;

    AggregateEventShards<PFilmEvent> FILM_EVENT_TAG =
            AggregateEventTag.sharded(PFilmEvent.class, NUM_SHARDS);

    @Override
    default AggregateEventShards<PFilmEvent> aggregateTag() {
        return FILM_EVENT_TAG;
    }

    @Value
    class FilmCreated implements PFilmEvent {

        PFilm film;

        @JsonCreator
        public FilmCreated(PFilm film) {
            this.film = film;
        }
    }
}
