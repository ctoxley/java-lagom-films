package com.tentoltd.films.inventory;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import java.util.UUID;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;

public interface InventoryService extends Service {

    ServiceCall<Film, Film> create();

    ServiceCall<NotUsed, Source<Film, ?>> list();

    ServiceCall<NotUsed, Film> get(UUID id);

    default Descriptor descriptor() {
        return named("inventory")
                .withCalls(restCall(Method.GET, "/inventory", this::list))
                .withCalls(restCall(Method.GET, "/inventory/:id", this::get))
                .withCalls(restCall(Method.POST, "/inventory", this::create));
    }
}