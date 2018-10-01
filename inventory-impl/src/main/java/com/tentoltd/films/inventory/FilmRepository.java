package com.tentoltd.films.inventory;

import akka.Done;
import akka.stream.javadsl.Source;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import com.tentoltd.films.inventory.entity.PFilm;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide.completedStatements;
import static com.tentoltd.films.inventory.entity.PFilm.Status.CREATED;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class FilmRepository {

    private static final String STORE = "films";

    private enum Store {
        id("TEXT"), title("TEXT"), category("TEXT");

        private final String type;

        Store(String type) {
            this.type = type;
        }

        static String select() {
            return format("SELECT %s FROM %s;", fieldsAsCsv(), STORE);
        }

        static String createStore() {
            return format("CREATE TABLE IF NOT EXISTS %s (%s, PRIMARY KEY (%s))", STORE, fieldsAsCsvWithTypes(), id.name());
        }

        static String insert() {
            return format("INSERT INTO %s (%s) VALUES (%s)", STORE, fieldsAsCsv(), fieldsAsQuestionMarks());
        }

        private static Object fieldsAsQuestionMarks() {
            return Stream.of(values()).map(e -> "?").collect(Collectors.joining(", "));
        }

        private static String fieldsAsCsv() {
            return Stream.of(values()).map(Enum::name).collect(Collectors.joining(", "));
        }

        private static String fieldsAsCsvWithTypes() {
            return Stream.of(values()).map(e -> e.name() + " " + e.type).collect(Collectors.joining(", "));
        }
    }

    private final CassandraSession cassandraSession;
    private PreparedStatement filmsInsertStatement = null;

    @Inject
    public FilmRepository(CassandraSession cassandraSession) {
        this.cassandraSession = cassandraSession;
    }

    public CompletionStage<List<BoundStatement>> store(PFilm film) {
        BoundStatement boundStatement = filmsInsertStatement.bind();
        boundStatement.setString(Store.id.name(), film.getId().toString());
        boundStatement.setString(Store.title.name(), film.getTitle());
        boundStatement.setString(Store.category.name(), film.getCategory());
        return completedStatements(asList(boundStatement));
    }

    public Source<PFilm, ?> list() {
        return cassandraSession.select(Store.select()).map(this::rowToPFilm);
    }

    public CompletionStage<Done> globalPrepare() {
        return cassandraSession.executeCreateTable(Store.createStore());
    }

    public CompletionStage<Done> prepare() {
        return cassandraSession.prepare(Store.insert())
                .thenApply(ps -> {
                    this.filmsInsertStatement = ps;
                    return Done.getInstance();
                });
    }

    private PFilm rowToPFilm(Row row) {
        UUID id = UUID.fromString(row.getString(Store.id.name()));
        String title = row.getString(Store.title.name());
        String category = row.getString(Store.category.name());
        return new PFilm(id, title, category, CREATED);
    }
}
