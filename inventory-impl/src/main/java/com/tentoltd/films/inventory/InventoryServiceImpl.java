package com.tentoltd.films.inventory;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.NotFound;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.tentoltd.films.inventory.entity.PFilm;
import com.tentoltd.films.inventory.entity.PFilmCommand;
import com.tentoltd.films.inventory.entity.PFilmCommand.CreateFilm;
import com.tentoltd.films.inventory.entity.PFilmCommand.GetFilm;
import com.tentoltd.films.inventory.entity.PFilmEntity;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class InventoryServiceImpl implements InventoryService {

    private final PersistentEntityRegistry persistentEntityRegistry;
    private final FilmRepository filmRepository;
    private final FilmTransformer filmTransformer;

    @Inject
    public InventoryServiceImpl(PersistentEntityRegistry persistentEntityRegistry, ReadSide readSide,
                                FilmTransformer filmTransformer, FilmRepository filmRepository) {

        this.persistentEntityRegistry = persistentEntityRegistry;
        this.filmRepository = filmRepository;
        this.filmTransformer = filmTransformer;
        persistentEntityRegistry.register(PFilmEntity.class);
        readSide.register(FilmEventProcessor.class);
    }

    @Override
    public ServiceCall<Film, Film> create() {
        return film -> {
            PFilm pFilm = filmTransformer.fromApi(film);
            return entityRef(pFilm.getId())
                    .ask(new CreateFilm(pFilm))
                    .thenApply(done -> filmTransformer.toApi(pFilm));
        };
    }

    @Override
    public ServiceCall<NotUsed, Source<Film, ?>> list() {
        return request -> {
            Source<Film, ?> films = filmRepository.list().map(filmTransformer::toApi);
            return CompletableFuture.completedFuture(films);
        };
    }

    @Override
    public ServiceCall<NotUsed, Film> get(UUID id) {
        return req -> entityRef(id).ask(GetFilm.INSTANCE).thenApply(maybeFilm -> {
            if (maybeFilm.isPresent()) {
                return filmTransformer.toApi(maybeFilm.get());
            } else {
                throw new NotFound("Item " + id + " not found");
            }
        });
    }

    private PersistentEntityRef<PFilmCommand> entityRef(UUID filmId) {
        return persistentEntityRegistry.refFor(PFilmEntity.class, filmId.toString());
    }
}
