package com.tentoltd.films.inventory.entity;

import akka.Done;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity.InvalidCommandException;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver.Outcome;
import com.tentoltd.films.inventory.entity.PFilmCommand.CreateFilm;
import com.tentoltd.films.inventory.entity.PFilmEvent.FilmCreated;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static com.tentoltd.films.inventory.entity.PFilmTestData.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class PFilmEntityTest {

    private static final String ENTITY_ID = "entityId";
    private static final int ONLY_ONE_EVENT_SHOULD_HAVE_BEEN_RAISED = 1;
    private static final int NO_EVENTS_SHOULD_HAVE_BEEN_RAISED = 0;
    private static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    private PersistentEntityTestDriver<PFilmCommand, PFilmEvent, PFilmState> driver;
    private PFilm film;

    @Before
    public void eachTest() throws Exception {
        driver = new PersistentEntityTestDriver<>(system, new PFilmEntity(), ENTITY_ID);
        film = aFilm();
    }

    @Test
    public void getFilm() {
        UUID filmId = runCreateFilmCommandAndGetId(film);
        Outcome<PFilmEvent, PFilmState> outcome = driver.run(PFilmCommand.GetFilm.INSTANCE);

        Optional<PFilm> gotFilm = (Optional<PFilm>) outcome.getReplies().iterator().next();
        assertThat(gotFilm.isPresent(), is(true));
        assertThat(gotFilm.get().getId(), is(not(nullValue())));
        assertThat(gotFilm.get().getTitle(), is(film.getTitle()));
        assertThat(gotFilm.get().getCategory(), is(film.getCategory()));
    }

    @Test
    public void createFilm() {
        Outcome<PFilmEvent, PFilmState> outcome = runCreateFilmCommand(film);

        assertThat(outcome.events().size(), is(ONLY_ONE_EVENT_SHOULD_HAVE_BEEN_RAISED));
        assertThat(outcome.getReplies().iterator().next(), is(Done.getInstance()));

        FilmCreated filmEvent = (FilmCreated) outcome.events().iterator().next();
        assertThat(filmEvent.getFilm().getId(), is(not(nullValue())));
        assertThat(filmEvent.getFilm().getTitle(), is(film.getTitle()));
        assertThat(filmEvent.getFilm().getCategory(), is(film.getCategory()));
    }

    @Test
    public void createFilmInvalidId() {
        Outcome<PFilmEvent, PFilmState> outcome = runCreateFilmCommand(aFilmWithoutId());
        assertThatOnlyInvalidCommandExceptionOccurred(outcome, "A film must have an ID.");
    }

    @Test
    public void createFilmInvalidTitle() {
        Outcome<PFilmEvent, PFilmState> outcome = runCreateFilmCommand(aFilmWithoutTitle());
        assertThatOnlyInvalidCommandExceptionOccurred(outcome, "A film must have a title.");
    }

    @Test
    public void createFilmInvalidCategory() {
        Outcome<PFilmEvent, PFilmState> outcome = runCreateFilmCommand(aFilmWithoutCategory());
        assertThatOnlyInvalidCommandExceptionOccurred(outcome, "A film must have a valid category of " + "[new|normal" +
                "|old|]. Category given [].");
    }

    private void assertThatOnlyInvalidCommandExceptionOccurred(Outcome<PFilmEvent, PFilmState> outcome,
                                                               String expectedErrorMessage) {

        assertThat(outcome.events().size(), is(NO_EVENTS_SHOULD_HAVE_BEEN_RAISED));
        InvalidCommandException invalidCommandException =
                (InvalidCommandException) outcome.getReplies().iterator().next();
        assertThat(invalidCommandException.getMessage(), is(expectedErrorMessage));
    }

    private Outcome<PFilmEvent, PFilmState> runCreateFilmCommand(PFilm film) {
        return driver.run(new CreateFilm(film));
    }

    private UUID runCreateFilmCommandAndGetId(PFilm film) {
        Outcome<PFilmEvent, PFilmState> outcome = runCreateFilmCommand(film);
        FilmCreated filmEvent = (FilmCreated) outcome.events().iterator().next();
        return filmEvent.getFilm().getId();
    }
}