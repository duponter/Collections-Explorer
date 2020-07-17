package edu.boardgames.collections.explorer;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.BoardGameGeek;
import edu.boardgames.collections.explorer.domain.Copy;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollection;
import edu.boardgames.collections.explorer.domain.PlayerCount;
import edu.boardgames.collections.explorer.infrastructure.Async;
import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;
import org.apache.commons.lang3.StringUtils;

import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
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
	public String play(@QueryParam("geekbuddies") String geekbuddies, @QueryParam("bestWith") Integer bestWith) {
		String[] usernames = StringUtils.split(geekbuddies, ",");
		Predicate<BoardGame> bestWithFilter = bestWith != null ? new PlayerCount(bestWith)::bestOnly : bg -> true;
		LOGGER.log(Level.INFO, String.format("Search collections of %s to play a best with %s game", Arrays.toString(usernames), bestWith));
		String collections = streamOwnedBoardGames(BggInit.get().geekBuddies().withUsername(usernames))
				.filter(copy -> bestWithFilter.test(copy.boardGame()))
				.map(copy -> BoardGameRender.playInfo(copy.boardGame(), copy.collection().name()))
				.sorted()
				.collect(Collectors.joining("\n"));
		return String.format("Search collections of %s to play a best with %d game%n%n%s", Arrays.toString(usernames), bestWith, collections);
	}

	@GET
	@Path("/current")
	@Produces(MediaType.TEXT_PLAIN)
	public String current(@QueryParam("collections") String collections, @QueryParam("bestWith") Integer bestWith) {
		String[] collectionNames = StringUtils.split(collections, ",");
		Predicate<BoardGame> bestWithFilter = bestWith != null ? new PlayerCount(bestWith)::bestOnly : bg -> true;
		LOGGER.log(Level.INFO, String.format("Search currently playable collections of %s to play a best with %s game", Arrays.toString(collectionNames), bestWith));

		BoardGameCollection playableCollection = BggInit.get()
		                                                 .collections()
		                                                 .withNames(collectionNames);
		String boardGames = playableCollection.boardGameCopies().stream()
				.filter(copy -> bestWithFilter.test(copy.boardGame()))
				.map(copy -> BoardGameRender.playInfo(copy.boardGame(), copy.collection().name()))
				.sorted()
				.collect(Collectors.joining("\n"));
		return String.format("Search collections of %s to play a best with %d game%n%n%s", Arrays.toString(collectionNames), bestWith, boardGames);
	}

	@GET
	@Path("/wanttoplay/{geekbuddy}")
	@Produces(MediaType.TEXT_PLAIN)
	public String wantToPlay(@PathParam("geekbuddy") String geekbuddy, @QueryParam("bestWith") Integer bestWith) {
		LOGGER.log(Level.INFO, String.format("Search collections of all geekbuddies for %s's want-to-play best with %s games", geekbuddy, bestWith));

		BoardGameGeek bgg = BggInit.get();
		List<BoardGame> wantToPlay = bgg.geekBuddies()
		                                .one(geekbuddy)
		                                .wantToPlayCollection();
		LOGGER.log(Level.INFO, String.format("Collection fetched: %s wants to play %d boardgames.", geekbuddy, wantToPlay.size()));

		Map<String, String> availableCollections = fetchAvailableCollections(bgg.geekBuddies().all())
				.entrySet()
				.stream()
				.map(entry -> Map.entry(entry.getKey().id(), entry.getValue()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		LOGGER.log(Level.INFO, String.format("All owned collections fetched group per boardgame: %d", availableCollections.size()));

		String copies = wantToPlay.stream()
		                          .filter(bestWith == null ? always -> true : new PlayerCount(bestWith)::recommendedOnly)
		                          .map(boardGame -> {
			                          String owners = availableCollections.getOrDefault(boardGame.id(), "nobody");
			                          return BoardGameRender.playInfo(boardGame, owners);
		                          })
		                          .filter(line -> !line.endsWith("nobody"))
		                          .sorted()
		                          .collect(Collectors.joining("\n"));
		LOGGER.log(Level.INFO, "All owned collections matched against want to play collection");
		return String.format("Search collections of all geekbuddies for %s's want-to-play best with %d games%n%n%s", geekbuddy, bestWith, copies);
	}

	private Map<BoardGame, String> fetchAvailableCollections(List<GeekBuddy> all) {
		return streamOwnedBoardGames(all).collect(Collectors.groupingBy(Copy::boardGame, Collectors.mapping(Copy::collection, Collectors.mapping(BoardGameCollection::name, Collectors.joining(", ")))));
	}

	private Stream<Copy> streamOwnedBoardGames(List<GeekBuddy> geekBuddies) {
		AtomicInteger mergedCollectionsSize = new AtomicInteger(0);
		Function<GeekBuddy, List<Copy>> owned = geekBuddy -> {
			List<BoardGame> ownedCollection = geekBuddy.ownedCollection();
			LOGGER.log(Level.DEBUG, String.format("Size owned collection %s: %d", geekBuddy.name(), ownedCollection.size()));
			mergedCollectionsSize.updateAndGet(prev -> prev += ownedCollection.size());
			return new GeekBuddyCollection(geekBuddy, ownedCollection).boardGameCopies();
		};
		Stream<Copy> copies = Async.map(geekBuddies.stream(), owned).flatMap(List::stream);
		LOGGER.log(Level.INFO, String.format("All owned collections merged: %d", mergedCollectionsSize.get()));
		return copies;
	}

	@GET
	@Path("/lookup/{bgId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String lookup(@PathParam("bgId") String boardGameId) {
		LOGGER.log(Level.INFO, String.format("Search collections of all geekbuddies for game %s", boardGameId));

		String copies = streamOwnedBoardGames(BggInit.get()
		                                             .geekBuddies()
		                                             .all())
				.filter(copy -> StringUtils.equals(copy.boardGame()
				                                       .id(), boardGameId))
//				.collect(Collectors.groupingBy(Copy::boardGame, Collectors.mapping(Copy::owner, Collectors.mapping(GeekBuddy::name, Collectors.joining(", ")))))
//				.entrySet().stream()
//				.map(entry -> BoardGameRender.playInfo(entry.getKey(), entry.getValue()))
				.map(copy -> BoardGameRender.playInfo(copy.boardGame(), copy.collection().name()))
				.sorted()
				.collect(Collectors.joining("\n"));
		return String.format("Search collections of all geekbuddies for game %s%n%n%s", boardGameId, copies);
	}
}
