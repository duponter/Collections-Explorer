package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.net.http.HttpClient;
import java.util.function.Supplier;

public class GenericBggRequest extends BggRequest<GenericBggRequest> {
    GenericBggRequest(BggUrlFactory urlFactory) {
        super(urlFactory);
    }

    GenericBggRequest(BggUrlFactory urlFactory, Supplier<HttpClient> httpClientSupplier) {
        super(urlFactory, httpClientSupplier);
    }

    @Override
    GenericBggRequest self() {
        return this;
    }
}
