package com.tentoltd.films.inventory;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class Films {

    List<Film> films;

    @JsonCreator
    public Films(List<Film> films) {
        this.films = new ArrayList<>(films);
    }
}
