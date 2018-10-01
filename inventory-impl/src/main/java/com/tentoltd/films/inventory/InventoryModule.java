package com.tentoltd.films.inventory;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

public class InventoryModule extends AbstractModule implements ServiceGuiceSupport {

    protected void configure() {
        bindService(InventoryService.class, InventoryServiceImpl.class);
    }
}
