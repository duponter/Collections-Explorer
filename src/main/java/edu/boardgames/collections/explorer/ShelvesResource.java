package edu.boardgames.collections.explorer;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.BoardGameGeek;
import edu.boardgames.collections.explorer.domain.Copy;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.domain.PlayerCount;
import edu.boardgames.collections.explorer.infrastructure.Async;
import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
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
	private static final Logger LOGGER = LoggerFactory.getLogger(ShelvesResource.class);

	@GET
	@Path("/play")
	@Produces(MediaType.TEXT_PLAIN)
	public String play(@QueryParam("geekbuddies") String geekbuddies, @QueryParam("bestWith") Integer bestWithFilter) {
		String[] usernames = StringUtils.split(geekbuddies, ",");
		Integer bestWith = ObjectUtils.defaultIfNull(bestWithFilter, usernames.length);
		LOGGER.info("Search collections of {} to play a best with {} game", usernames, bestWith);
		String collections = fetchAvailableCollections(BggInit.get().geekBuddies().withUsername(usernames))
				.entrySet().stream()
				.map(entry -> BoardGameRender.playInfo(entry.getKey(), entry.getValue()))
				.sorted()
				.collect(Collectors.joining("\n"));
		return String.format("Search collections of %s to play a best with %d game%n%n%s", Arrays.toString(usernames), bestWith, collections);
	}

	@GET
	@Path("/wanttoplay/{geekbuddy}")
	@Produces(MediaType.TEXT_PLAIN)
	public String wantToPlay(@PathParam("geekbuddy") String geekbuddy, @QueryParam("bestWith") Integer bestWith) {
		LOGGER.info("Search collections of all geekbuddies for {}'s want-to-play best with {} games", geekbuddy, bestWith);

		BoardGameGeek bgg = BggInit.get();
		Stream<String> wantToPlayIds = bgg.geekBuddies().one(geekbuddy).wantToPlayCollection().stream().map(BoardGame::id);
		List<BoardGame> wantToPlay = bgg.boardGames().withIds(wantToPlayIds);
		LOGGER.info("Collection fetched: {} wants to play {} boardgames.", geekbuddy, wantToPlay.size());

		Map<String, String> availableCollections = fetchAvailableCollections(bgg.geekBuddies().all()).entrySet().stream()
				.map(entry -> Map.entry(entry.getKey().id(), entry.getValue()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		LOGGER.info("All owned collections fetched group per boardgame: {}", availableCollections.size());

		String copies = wantToPlay.stream()
				.filter(bestWith == null ? always -> true : new PlayerCount(bestWith)::recommendedOnly)
				.map(boardGame -> {
					String owners = availableCollections.getOrDefault(boardGame.id(), "nobody");
					return BoardGameRender.playInfo(boardGame, owners);
				})
				.filter(line -> !line.endsWith("nobody"))
				.sorted()
				.collect(Collectors.joining("\n"));
		LOGGER.info("All owned collections matched against want to play collection");
		return String.format("Search collections of all geekbuddies for %s's want-to-play best with %d games%n%n%s", geekbuddy, bestWith, copies);
	}

	private Map<BoardGame, String> fetchAvailableCollections(List<GeekBuddy> all) {
		return fetchOwnedBoardGames(all).stream()
				.collect(Collectors.groupingBy(Copy::boardGame, Collectors.mapping(Copy::owner, Collectors.mapping(GeekBuddy::name, Collectors.joining(", ")))));
	}

	private List<Copy> fetchOwnedBoardGames(List<GeekBuddy> all) {
		Function<GeekBuddy, List<Copy>> owned = geekBuddy -> Copy.from(geekBuddy, geekBuddy.ownedCollection());
		List<Copy> list = Async.map(all.stream(), owned)
				.flatMap(List::stream)
				.collect(Collectors.toList());
		LOGGER.info("All owned collections merged: {}", list.size());
		return list;
	}

	@GET
	@Path("/lookup/{bgId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String lookup(@PathParam("bgId") String boardGameId) {
		LOGGER.info("Search collections of all geekbuddies for game {}", boardGameId);

		String copies = fetchOwnedBoardGames(BggInit.get().geekBuddies().all())
				.stream()
				.filter(copy -> StringUtils.equals(copy.boardGame().id(), boardGameId))
//				.collect(Collectors.groupingBy(Copy::boardGame, Collectors.mapping(Copy::owner, Collectors.mapping(GeekBuddy::name, Collectors.joining(", ")))))
//				.entrySet().stream()
//				.map(entry -> BoardGameRender.playInfo(entry.getKey(), entry.getValue()))
				.map(copy -> BoardGameRender.playInfo(copy.boardGame(), copy.owner().name()))
				.sorted()
				.collect(Collectors.joining("\n"));
		return String.format("Search collections of all geekbuddies for game %s%n%n%s", boardGameId, copies);
	}
}