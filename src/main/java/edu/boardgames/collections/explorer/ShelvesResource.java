package edu.boardgames.collections.explorer;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.Copy;
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
		LOGGER.log(Level.INFO, String.format("Search currently playable collections %s to play a best with %s game", Arrays.toString(collectionNames), ObjectUtils.defaultIfNull(bestWith, "n/a")));
		String boardGames = displayMultiLineString(
				BggInit.get().collections().withNames(collectionNames),
				toCopyFilter(bestWithFilter(bestWith))
		);
		return String.format("Search collections %s to play a best with %s game%n%n%s", Arrays.toString(collectionNames), ObjectUtils.defaultIfNull(bestWith, "n/a"), boardGames);
	}

	@GET
	@Path("/wanttoplay/{geekbuddy}")
	@Produces(MediaType.TEXT_PLAIN)
	public String wantToPlay(@PathParam("geekbuddy") String geekbuddy, @QueryParam("bestWith") Integer bestWith) {
		LOGGER.log(Level.INFO, String.format("Search collections of all geekbuddies for %s's want-to-play best with %s games", geekbuddy, bestWith));

		List<BoardGame> wantToPlay = BggInit.get().geekBuddies().one(geekbuddy).wantToPlayCollection();
		LOGGER.log(Level.INFO, String.format("Collection fetched: %s wants to play %d boardgames.", geekbuddy, wantToPlay.size()));

		String copies = displayMultiLineString(
				BggInit.get().collections().all(),
				toCopyFilter(bestWithFilter(bestWith).and(wantToPlay::contains))
		);
		LOGGER.log(Level.INFO, "All owned collections matched against want to play collection");
		return String.format("Search collections of all geekbuddies for %s's want-to-play best with %d games%n%n%s", geekbuddy, bestWith, copies);
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
		LOGGER.log(Level.INFO, String.format("Searching all collections for game %s", boardGameName));
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
