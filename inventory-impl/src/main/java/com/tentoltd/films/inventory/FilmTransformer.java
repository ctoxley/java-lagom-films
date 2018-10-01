package com.tentoltd.films.inventory;

import com.datastax.driver.core.utils.UUIDs;
import com.tentoltd.films.inventory.entity.PFilm;

import java.util.Optional;
import java.util.UUID;

import static com.tentoltd.films.inventory.entity.PFilm.Status.CREATED;

public class FilmTransformer {

    public PFilm fromApi(Film film) {
        UUID id = Optional.ofNullable(film.getId()).orElse(UUIDs.timeBased());
        return new PFilm(id, film.getTitle(), film.getCategory(), CREATED);
    }

    public Film toApi(PFilm film) {
        return new Film(film.getId(), film.getTitle(), film.getCategory());
    }
}
