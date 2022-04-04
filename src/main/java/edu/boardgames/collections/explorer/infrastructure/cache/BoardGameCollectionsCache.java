package edu.boardgames.collections.explorer.infrastructure.cache;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.BoardGameCollectionGroup;
import edu.boardgames.collections.explorer.domain.BoardGameCollections;
import edu.boardgames.collections.explorer.domain.GeekBuddies;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.domain.GeekLists;
import io.vavr.Lazy;

public class BoardGameCollectionsCache implements BoardGameCollections {
    private final Cache<String, Lazy<BoardGameCollection>> collections = Caffeine.newBuilder().build();

	public BoardGameCollectionsCache(GeekBuddies geekBuddies, GeekLists geekLists) {
        geekBuddies.all().forEach(geekBuddy -> collections.put(StringUtils.lowerCase(geekBuddy.username()), Lazy.of(() -> fromGeekBuddy(geekBuddy))));
        geekLists.all().forEach(geekList -> collections.put(geekList.id(), Lazy.of(geekList::asCollection)));

        collections.put("mine", Lazy.of(() -> this.asGroup("mine", "duponter", "274761")));
        collections.put("bareelstraat", Lazy.of(() -> this.asGroup("bareelstraat", "mine", "wouteraerts", "jarrebesetoert")));
        collections.put("fmlimited", Lazy.of(() -> this.asGroup("fmlimited", "mine", "bartie", "de rode baron", "edou", "evildee", "svennos", "turtler6")));
        collections.put("sirplayalot", Lazy.of(() -> this.asGroup("sirplayalot", "wallofshame", "leys")));
//		collections.put("forum", this.asGroup("forum", "forummortsel", "ffed"));
    }

    private BoardGameCollection fromGeekBuddy(GeekBuddy geekBuddy) {
        BoardGameCollection owned = geekBuddy.ownedCollection();
        return new BoardGameCollection() {
            @Override
            public String id() {
                return owned.id();
            }

            @Override
            public String name() {
                return geekBuddy.name();
            }

            @Override
            public List<BoardGame> boardGames() {
                return owned.boardGames();
            }
        };
    }

    @Override
    public BoardGameCollection all() {
        return new BoardGameCollectionGroup("all", collections.asMap()
                .values()
                .toArray(BoardGameCollection[]::new));
    }

    @Override
    public BoardGameCollection one(String name) {
        return collections.getIfPresent(StringUtils.lowerCase(name)).get();
	}

	@Override
	public BoardGameCollection withNames(String... names) {
		return this.asGroup(Arrays.toString(names), names);
	}

	private BoardGameCollection asGroup(String groupName, String... names) {
		BoardGameCollection[] subCollections = Arrays.stream(names)
		                                             .map(this::one)
		                                             .toArray(BoardGameCollection[]::new);
		return new BoardGameCollectionGroup(groupName, subCollections);
	}
}
