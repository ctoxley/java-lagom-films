package com.tentoltd.films.inventory;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Value;

import java.util.UUID;

@Value
public class Film {

    UUID id;
    String title;
    String category;

    @JsonCreator
    public Film(UUID id, String title, String category) {
        this.id = id;
        this.title = title;
        this.category = category;
    }
}
