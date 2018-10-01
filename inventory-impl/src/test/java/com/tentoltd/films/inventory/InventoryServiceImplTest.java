package com.tentoltd.films.inventory;

import com.lightbend.lagom.javadsl.testkit.ServiceTest.TestServer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.startServer;
import static com.tentoltd.films.inventory.FilmTestData.aNewFilm;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InventoryServiceImplTest {

    private static final int ONLY_ONE_FILM_CREATED = 1;
    private static TestServer server;

    @BeforeClass
    public static void setUp() {
        server = startServer(defaultSetup().withCluster(false).withCassandra(true));
    }

    @AfterClass
    public static void tearDown() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    private InventoryService inventoryService;

    @Before
    public void eachTest() throws Exception {
        inventoryService = server.client(InventoryService.class);
    }

    @Test
    public void createThenGetThenList() throws Exception {
        Film filmToAdd = aNewFilm();
        Film filmAdded = inOneSecondGetResultOf(inventoryService.create().invoke(filmToAdd));
        assertThat(filmAdded.getTitle(), is(filmToAdd.getTitle()));
        assertThat(filmAdded.getCategory(), is(filmToAdd.getCategory()));

        Film filmGot = inOneSecondGetResultOf(inventoryService.get(filmAdded.getId()).invoke());
        assertThat(filmGot.getId(), is(filmAdded.getId()));

// TODO: Find out why this does not work.
//        Source<Film, ?> filmsSource = inOneSecondGetResultOf(inventoryService.list().invoke());
//        CompletionStage<List<Film>> filmsSourced = filmsSource.runWith(Sink.seq(), server.materializer());
//        List<Film> films = inOneSecondGetResultOf(filmsSourced);
//        assertThat(films.size(), is(ONLY_ONE_FILM_CREATED));
//        assertThat(films.iterator().next().getTitle(), is(filmToAdd.getTitle()));
    }

    private <T> T inOneSecondGetResultOf(CompletionStage<T> invoke) throws Exception {
        return invoke.toCompletableFuture().get(1, TimeUnit.MINUTES);
    }
}
