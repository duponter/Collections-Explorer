package edu.boardgames.collections.explorer;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.Copy;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.domain.PlayerCount;
import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/shelves")
public class ShelvesResource {
	private static final System.Logger LOGGER = System.getLogger(ShelvesResource.class.getName());

	@GET
	@Path("/play")
	@Produces(MediaType.TEXT_PLAIN)
	public String current(@QueryParam("collections") String collections, @QueryParam("bestWith") Integer bestWith) {
		String[] collectionNames = StringUtils.split(collections, ",");
		LOGGER.log(Level.INFO, "Search currently playable collections {0} to play a best with {1} game", Arrays.toString(collectionNames), ObjectUtils.defaultIfNull(bestWith, "n/a"));
		String boardGames = displayMultiLineString(
				BggInit.get().collections().withNames(collectionNames),
				toCopyFilter(bestWithFilter(bestWith))
		);
		return String.format("Search collections %s to play a best with %s game%n%n%s", Arrays.toString(collectionNames), ObjectUtils.defaultIfNull(bestWith, "n/a"), boardGames);
	}

	@GET
	@Path("/wanttoplay/{geekbuddy}")
	@Produces(MediaType.TEXT_PLAIN)
	public String wantToPlay(@PathParam("geekbuddy") String geekbuddy, @QueryParam("bestWith") Integer bestWith, @QueryParam("includeRated") Integer includeRated) {
		LOGGER.log(Level.INFO, "Search collections of all geekbuddies for {0}'s want-to-play best with {1,number,integer} games", geekbuddy, bestWith);

		GeekBuddy buddy = BggInit.get().geekBuddies().one(geekbuddy);
		List<BoardGame> wantToPlay = buddy.wantToPlayCollection();
		LOGGER.log(Level.INFO, "Collection fetched: {0} wants to play {1,number,integer} boardgames.", geekbuddy, wantToPlay.size());
		if (includeRated != null) {
			List<BoardGame> rated = buddy.ratedCollection(includeRated);
			LOGGER.log(Level.INFO, "Collection fetched: {0} rated {1,number,integer} boardgames {1,number,integer} or more.", geekbuddy, rated.size(), includeRated);
			wantToPlay = Stream.concat(wantToPlay.stream(), rated.stream()).collect(Collectors.toList());
		}

		String copies = displayMultiLineString(
				BggInit.get().collections().all(),
				toCopyFilter(bestWithFilter(bestWith).and(wantToPlay::contains))
		);
		LOGGER.log(Level.INFO, "All owned collections matched against want to play collection");
		return String.format("Search collections of all geekbuddies for %s's want-to-play best with %d games%n%n%s", geekbuddy, bestWith, copies);
	}

	@GET
	@Path("/wanttoreplay/{geekbuddy}")
	@Produces(MediaType.TEXT_PLAIN)
	public String wantToReplay(@PathParam("geekbuddy") String geekbuddy, @QueryParam("bestWith") Integer bestWith, @QueryParam("minimallyRated") Integer minimallyRated) {
		LOGGER.log(Level.INFO, "{0} wants to replay best with {1,number,integer} games minimally rated {2,number,integer}", geekbuddy, bestWith, minimallyRated);

		GeekBuddy buddy = BggInit.get().geekBuddies().one(geekbuddy);
			List<BoardGame> rated = buddy.ratedCollection(minimallyRated);
			LOGGER.log(Level.INFO, "Collection fetched: {0} rated {1,number,integer} boardgames {1,number,integer} or more.", geekbuddy, rated.size(), minimallyRated);

		String copies = rated.stream().map(BoardGameRender::playInfo).collect(Collectors.joining("\n"));
		return String.format("Show replay options for %s's want-to-play best with %d games rated at least %d%n%n%s", geekbuddy, bestWith, minimallyRated, copies);
	}

	@GET
	@Path("/lookup/{bgId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String lookup(@PathParam("bgId") String boardGameId) {
		List<BoardGame> boardGames = BggInit.get().boardGames().withIds(Stream.of(boardGameId));
		if (boardGames.isEmpty()) {
			return String.format("Board Game with id [%s] not found or does not exists on BGG", boardGameId);
		}

		String boardGameName = boardGames.stream().findFirst().map(BoardGame::name).orElseThrow();
		LOGGER.log(Level.INFO, "Searching all collections for game {0}", boardGameName);
		String copies = displayMultiLineString(
				BggInit.get().collections().all(),
				copy -> StringUtils.equals(copy.boardGame().id(), boardGameId)
		);
		return String.format("Searched all known collections for game %s:%n%n%s", boardGameName, StringUtils.defaultIfBlank(copies.trim(), ">>> not found in known collections"));
	}

	private Predicate<Copy> toCopyFilter(Predicate<BoardGame> filter) {
		return lift(filter, Copy::boardGame);
	}

	private <T, R> Predicate<R> lift(Predicate<T> filter, Function<R, T> mapper) {
		return subject -> filter.test(mapper.apply(subject));
	}

	private Predicate<BoardGame> bestWithFilter(Integer bestWith) {
		return bestWith != null ? new PlayerCount(bestWith)::bestOnly : bg -> true;
	}

	private String displayMultiLineString(BoardGameCollection collection, Predicate<Copy> filter) {
		return this.displayMultiLineString(collection.boardGameCopies()
		                                             .stream()
		                                             .filter(filter));
	}

	private String displayMultiLineString(Stream<Copy> copies) {
		return copies.collect(Collectors.groupingBy(Copy::boardGame, Collectors.mapping(Copy::collection, Collectors.mapping(BoardGameCollection::name, Collectors.toCollection(TreeSet::new)))))
		             .entrySet()
		             .stream()
		             .map(entry -> BoardGameRender.playInfo(entry.getKey(), String.join(", ", entry.getValue())))
		             .sorted()
		             .collect(Collectors.joining("\n"));
	}
}
