package edu.boardgames.collections.explorer.infrastructure.cache;

import java.time.Duration;
import java.util.Objects;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.GeekList;
import edu.boardgames.collections.explorer.domain.GeekListCollections;

public class GeekListCollectionsCache implements GeekListCollections {
    private final Cache<String, BoardGameCollection> collections;
    private final GeekListCollections delegate;

    public GeekListCollectionsCache(GeekListCollections delegate) {
        this.delegate = Objects.requireNonNull(delegate);
        this.collections = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(5))
                .build();
    }

    @Override
    public BoardGameCollection resolved(GeekList geekList) {
        return collections.get(geekList.id(), id -> delegate.resolved(geekList));
    }
}
