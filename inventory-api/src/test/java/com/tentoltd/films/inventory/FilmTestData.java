package com.tentoltd.films.inventory;

import java.util.UUID;

import static org.mockito.internal.progress.SequenceNumber.next;

public class FilmTestData {

    public static final Film aNewFilm() {
        return new Film(UUID.randomUUID(), "title" + next(), "new");
    }

    public static final Film aNormalFilm() {
        return new Film(UUID.randomUUID(), "title" + next(), "normal");
    }

    public static final Film aOldFilm() {
        return new Film(UUID.randomUUID(), "title" + next(), "old");
    }
}