package edu.boardgames.collections.explorer.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.BoardGameCollectionGroup;
import edu.boardgames.collections.explorer.domain.BoardGameCollections;
import edu.boardgames.collections.explorer.domain.GeekBuddies;
import edu.boardgames.collections.explorer.domain.GeekLists;
import io.vavr.Lazy;

import java.util.Arrays;

public class BoardGameCollectionsCache implements BoardGameCollections {
	private final Cache<String, BoardGameCollection> collections = Caffeine.newBuilder()
	                                                                       .build();

	public BoardGameCollectionsCache(GeekBuddies geekBuddies, GeekLists geekLists) {
		geekBuddies.all().forEach(geekBuddy -> collections.put(geekBuddy.username(), new LazyBoardGameCollection(geekBuddy.username(), geekBuddy.name(), Lazy.of(geekBuddy::ownedCollection))));
		geekLists.all().forEach(geekList -> collections.put(geekList.id(), new LazyBoardGameCollection(geekList.id(), geekList.name(), Lazy.of(geekList::boardGames))));

		collections.put("mine", this.asGroup("mine", "duponter", "274761"));
		collections.put("bareelstraat", this.asGroup("bareelstraat", "mine", "WouterAerts", "jarrebesetoert"));
		collections.put("fmlimited", this.asGroup("fmlimited", "mine", "bartie", "de rode baron", "Edou", "evildee", "Svennos", "TurtleR6"));
		collections.put("sirplayalot", this.asGroup("sirplayalot", "wallofshame", "leys"));
		collections.put("forum", this.asGroup("forum", "ForumMortsel", "FFED"));
	}

	@Override
	public BoardGameCollection all() {
		return new BoardGameCollectionGroup("all", collections.asMap()
		                                                      .values()
		                                                      .toArray(BoardGameCollection[]::new));
	}

	@Override
	public BoardGameCollection one(String name) {
		return collections.getIfPresent(name);
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
