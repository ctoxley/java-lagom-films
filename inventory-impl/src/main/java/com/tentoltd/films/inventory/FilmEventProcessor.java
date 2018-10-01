package com.tentoltd.films.inventory;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.tentoltd.films.inventory.entity.PFilmEvent;
import com.tentoltd.films.inventory.entity.PFilmEvent.FilmCreated;
import org.pcollections.PSequence;

import javax.inject.Inject;

public class FilmEventProcessor extends ReadSideProcessor<PFilmEvent> {

    private final FilmRepository filmRepository;
    private final CassandraReadSide cassandraReadSide;

    @Inject
    public FilmEventProcessor(FilmRepository filmRepository, CassandraReadSide cassandraReadSide) {
        this.filmRepository = filmRepository;
        this.cassandraReadSide = cassandraReadSide;
    }

    @Override
    public ReadSideHandler<PFilmEvent> buildHandler() {
        CassandraReadSide.ReadSideHandlerBuilder<PFilmEvent> readSideBuilder = cassandraReadSide.builder("film-offset");
        readSideBuilder.setGlobalPrepare(filmRepository::globalPrepare);
        readSideBuilder.setPrepare(tag -> filmRepository.prepare());
        readSideBuilder.setEventHandler(FilmCreated.class, (fc) -> filmRepository.store(fc.getFilm()));
        return readSideBuilder.build();
    }

    @Override
    public PSequence<AggregateEventTag<PFilmEvent>> aggregateTags() {
        return PFilmEvent.FILM_EVENT_TAG.allTags();
    }
}
