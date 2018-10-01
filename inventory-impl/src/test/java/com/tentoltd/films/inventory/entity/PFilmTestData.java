package com.tentoltd.films.inventory.entity;

import com.tentoltd.films.inventory.entity.PFilm.Category;

import java.util.UUID;

import static com.tentoltd.films.inventory.entity.PFilm.Status.CREATED;

public class PFilmTestData {

    public static PFilm aFilm() {
        return new PFilm(UUID.randomUUID(), "title", Category.NEW.key(), CREATED);
    }

    public static PFilm aFilmWithoutId() {
        return new PFilm(null, "title", Category.NEW.key(), CREATED);
    }

    public static PFilm aFilmWithoutTitle() {
        return new PFilm(UUID.randomUUID(), "", Category.NEW.key(), CREATED);
    }

    public static PFilm aFilmWithoutCategory() {
        return new PFilm(UUID.randomUUID(), "title", "", CREATED);
    }
}
