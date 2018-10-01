package com.tentoltd.films.inventory.entity;

import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.tentoltd.films.inventory.entity.PFilm.Category;
import com.tentoltd.films.inventory.entity.PFilmCommand.CreateFilm;
import com.tentoltd.films.inventory.entity.PFilmCommand.GetFilm;
import com.tentoltd.films.inventory.entity.PFilmEvent.FilmCreated;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.tentoltd.films.inventory.entity.PFilm.Status.NOT_CREATED;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class PFilmEntity extends PersistentEntity<PFilmCommand, PFilmEvent, PFilmState> {

    @Override
    public Behavior initialBehavior(Optional<PFilmState> filmState) {
        PFilm.Status filmStatus = filmState.map(PFilmState::getStatus).orElse(NOT_CREATED);
        switch (filmStatus) {
            case CREATED: return filmPersistedForIdBehaviour(filmState.get());
            default: return noFilmPersistedForIdBehaviour();
        }
    }

    private Behavior noFilmPersistedForIdBehaviour() {
        BehaviorBuilder behaviorBuilder = newBehaviorBuilder(PFilmState.empty());
        setCommandHandlerForCreateFilm(behaviorBuilder);
        setEventHandlerForFilmCreated(behaviorBuilder);
        return behaviorBuilder.build();
    }

    private Behavior filmPersistedForIdBehaviour(PFilmState filmState) {
        BehaviorBuilder behaviorBuilder = newBehaviorBuilder(filmState);
        behaviorBuilder.setReadOnlyCommandHandler(GetFilm.class, this::getFilm);
        return behaviorBuilder.build();
    }

    private void setEventHandlerForFilmCreated(BehaviorBuilder behaviorBuilder) {
        behaviorBuilder.setEventHandlerChangingBehavior(FilmCreated.class,
                evt -> filmPersistedForIdBehaviour(PFilmState.create(evt.getFilm())));
    }

    private void setCommandHandlerForCreateFilm(BehaviorBuilder behaviorBuilder) {
        behaviorBuilder.setCommandHandler(CreateFilm.class, (cmd, ctx) -> {
            return ifFilmInvalidGenerateErrorMessages(cmd.getFilm()).map(m -> {
                ctx.invalidCommand(m);
                return ctx.done();
            }).orElse(ctx.thenPersist(new FilmCreated(cmd.getFilm()), evt -> ctx.reply(Done.getInstance())));
        });
    }

    private Optional<String> ifFilmInvalidGenerateErrorMessages(PFilm film) {
        Set<String> messages = new HashSet<>();
        if (film.getId() == null) {
            messages.add("A film must have an ID.");
        }
        if (isBlank(film.getTitle())) {
            messages.add("A film must have a title.");
        }
        if (film.hasValidCategory() == false) {
            messages.add(String.format("A film must have a valid category of [%s]. Category given [%s].",
                    Category.categoryStrs(), film.getCategory()));
        }
        return messages.isEmpty() ? Optional.empty() : Optional.of(messages.stream().collect(joining(", ")));
    }

    private void getFilm(GetFilm getFilm, ReadOnlyCommandContext<Optional<PFilm>> ctx) {
        ctx.reply(state().getOptionalFilm());
    }
}
