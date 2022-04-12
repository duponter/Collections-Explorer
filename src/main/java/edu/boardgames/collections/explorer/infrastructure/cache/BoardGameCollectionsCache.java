package edu.boardgames.collections.explorer.infrastructure.cache;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.BoardGameCollectionGroup;
import edu.boardgames.collections.explorer.domain.BoardGameCollections;
import edu.boardgames.collections.explorer.domain.GeekBuddies;
import edu.boardgames.collections.explorer.domain.GeekLists;
import io.vavr.Lazy;

public class BoardGameCollectionsCache implements BoardGameCollections {
    // TODO_EDU is this cache needed?
    private final Cache<String, Lazy<BoardGameCollection>> collections = Caffeine.newBuilder().build();

	public BoardGameCollectionsCache(GeekBuddies geekBuddies, GeekLists geekLists) {
        geekBuddies.all().forEach(geekBuddy -> collections.put(StringUtils.lowerCase(geekBuddy.username()), Lazy.of(() -> geekBuddy.ownedCollection().withName(geekBuddy.name()))));
        geekLists.all().forEach(geekList -> collections.put(geekList.id(), Lazy.of(geekList::asCollection)));

        collections.put("all", Lazy.of(() -> this.asGroup("all", collections.asMap().keySet().toArray(String[]::new))));
        collections.put("mine", Lazy.of(() -> this.asGroup("mine", "duponter", "274761")));
        collections.put("bareelstraat", Lazy.of(() -> this.asGroup("bareelstraat", "mine", "wouteraerts", "jarrebesetoert")));
        collections.put("fmlimited", Lazy.of(() -> this.asGroup("fmlimited", "mine", "bartie", "de rode baron", "edou", "evildee", "svennos", "turtler6")));
        collections.put("sirplayalot", Lazy.of(() -> this.asGroup("sirplayalot", "wallofshame", "leys")));
//		collections.put("forum", this.asGroup("forum", "forummortsel", "ffed"));
    }

    @Override
    public BoardGameCollection all() {
        return this.one("all");
    }

    @Override
    public BoardGameCollection withNames(String... names) {
        return this.asGroup(Arrays.toString(names), names);
    }

    private BoardGameCollection one(String name) {
        return collections.getIfPresent(StringUtils.lowerCase(name)).get();
    }

    private BoardGameCollection asGroup(String groupName, String... names) {
        BoardGameCollection[] subCollections = Arrays.stream(names)
                .map(this::one)
                .toArray(BoardGameCollection[]::new);
        return new BoardGameCollectionGroup(groupName, subCollections);
    }
}
