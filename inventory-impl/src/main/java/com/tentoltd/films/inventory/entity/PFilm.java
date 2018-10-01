package com.tentoltd.films.inventory.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

import java.util.UUID;
import java.util.stream.Stream;

import static com.tentoltd.films.inventory.entity.PFilm.Category.toCategory;
import static java.util.stream.Collectors.joining;

@Value
public class PFilm implements Jsonable {

    public enum Status {
        NOT_CREATED, CREATED;
    }

    public enum Category {
        NEW("new"), NORMAL("normal"), OLD("old"), UNKNOWN("");

        private final String key;

        Category(String key) {
            this.key = key;
        }

        static Category toCategory(String key) {
            return Stream.of(values())
                    .filter(fc -> fc.key.equals(key))
                    .findFirst()
                    .orElse(UNKNOWN);
        }

        static String categoryStrs() {
            return Stream.of(values())
                    .map(fc -> fc.key)
                    .collect(joining("|"));
        }

        public String key() {
            return key;
        }
    }

    UUID id;
    String title;
    String category;
    Status status;

    @JsonCreator
    public PFilm(UUID id, String title, String category, Status status) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.status = status;
    }

    public boolean hasValidCategory() {
        return Category.UNKNOWN != toCategory(category);
    }
}
