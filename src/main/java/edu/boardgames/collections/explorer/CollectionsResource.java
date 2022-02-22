package edu.boardgames.collections.explorer;

import java.io.IOException;
import java.io.Serializable;
import java.lang.System.Logger.Level;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.CollectedBoardGame;
import edu.boardgames.collections.explorer.domain.GeekBuddies;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.domain.GeekList;
import edu.boardgames.collections.explorer.domain.PlayerCount;
import edu.boardgames.collections.explorer.infrastructure.Async;
import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;
import edu.boardgames.collections.explorer.infrastructure.bgg.CollectionRequest;
import edu.boardgames.collections.explorer.infrastructure.bgg.GeekBuddiesBggInMemory;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import static java.util.Map.entry;

@Path("/collections")
public class CollectionsResource {
	private static final System.Logger LOGGER = System.getLogger(CollectionsResource.class.getName());

    @GET
	@Path("/geeklist/{geeklist}")
	@Produces(MediaType.TEXT_PLAIN)
	public String geeklist(@PathParam("geeklist") String geeklistId, @QueryParam("bestWith") Integer bestWith) {
		LOGGER.log(Level.INFO, String.format("Lookup boardgames in geeklist %s for best with %s", geeklistId, bestWith));
		GeekList geeklist = BggInit.get().geekLists().withId(geeklistId);
		LOGGER.log(Level.INFO, "Geeklist fetched, processing best with filter");

		String boardGames = geeklist.boardGames().stream()
				.filter(bestWith == null ? always -> true : new PlayerCount(bestWith)::recommendedOnly)
				.sorted(Comparator.comparing(BoardGame::name).thenComparing(BoardGame::year))
				.map(BoardGameRender::playInfo)
				.collect(Collectors.joining("\n"));
		return String.format("Search all board games for best with %d of geeklist %s%n%n%s", bestWith, geeklist.name(), boardGames);
	}

	@GET
	@Path("/{geekbuddy}/wanttoplay")
	@Produces(MediaType.TEXT_PLAIN)
	public String wantToPlay(@PathParam("geekbuddy") String geekbuddy, @QueryParam("bestWith") Integer bestWithFilter) {
		LOGGER.log(Level.INFO, String.format("Search collections of all geekbuddies for best with %s want-to-play games of %s", bestWithFilter, geekbuddy));
		String wantToPlay = geekBuddies().withUsername(geekbuddy).stream()
				.flatMap(buddy -> buddy.wantToPlayCollection().stream())
				.map(BoardGameRender::playInfo)
				.collect(Collectors.joining("\n"));
		return String.format("Search collections of all geekbuddies for best with %d want-to-play games of %s%n%n%s", bestWithFilter, geekbuddy, wantToPlay);
	}

	//https://google.github.io/flogger/best_practice

    @GET
	@Path("/xsl")
	@Produces(MediaType.TEXT_XML)
	public String xsl() throws URISyntaxException, IOException {
		return Files.readString(java.nio.file.Path.of(CollectionsResource.class.getResource("play-info.xsl").toURI()));
	}
	@GET
	@Path("/xml/{username}")
	@Produces(MediaType.TEXT_XML)
	public String xml(@PathParam("username") String username, @QueryParam("password") String password) {
        return new CollectionRequest(username, password).owned().asXml();
	}

	@GET
	@Path("/stream")
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public Publisher<String> publishers() {
//		List<Flowable<Pair<String, Stream<String>>>> flowables = Async.mapToFutures(users().values().stream(), toPair.andThen(pair -> mapRight(pair, nameStringExtractor))).stream().map(Flowable::fromFuture).collect(Collectors.toList());
//		return Flowable
//				.concat(flowables)
//				.flatMap(pair -> Flowable.fromIterable(pair.getRight().map(game -> String.format("%s (%s)", game, pair.getLeft())).collect(Collectors.toList())));
        return Flowable.just(geekBuddies().one("duponter"))
                .map(GeekBuddy::username)
                .map(this::usernameToCollection)
                .flatMap(pair -> Flowable.fromIterable(pair.getRight().map(game -> String.format("%s (%s)", game, pair.getLeft())).toList()));
    }

	@GET
	@Path("/users")
	@Produces(MediaType.TEXT_PLAIN)
    public String infoByIds(@QueryParam("usernames") String usernames, @QueryParam("firstnames") String firstnames, @QueryParam("playercount") Integer playercount, @QueryParam("maxtime") Integer maxtime) {
//		https://www.callicoder.com/java-8-completablefuture-tutorial/
//		http://tabulator.info/
        Set<String> collectedNames = Async.map(Arrays.stream(StringUtils.split(usernames, ",")), this::usernameToCollection)
                .flatMap(pair -> pair.getRight().map(game -> String.format("%s (%s)", game, pair.getLeft())))
                .collect(Collectors.toCollection(TreeSet::new));
        return "to implement via 1. CompletableFuture.allOf() and render via http://tabulator.info/\nids:\n" + StringUtils.join(collectedNames, "\n");
    }

    private Pair<String, Stream<String>> usernameToCollection(String username) {
        return Pair.of(
                username,
                new CollectionRequest(username).withStats().withoutExpansions().execute()
                        .map(this::toMap)
                        .map(Map::toString)
        );
    }

    private Map<String, Serializable> toMap(CollectedBoardGame cbg) {
        return Map.ofEntries(
                entry("id", cbg.id()),
                entry("name", cbg.name()),
                entry("originalName", cbg.originalName()),
                entry("year", cbg.year()),
                entry("rated", cbg.rated()),
                entry("rating", cbg.rating()),
                entry("owned", cbg.owned()),
                entry("previouslyOwned", cbg.previouslyOwned()),
                entry("forTrade", cbg.forTrade()),
                entry("wanted", cbg.wanted()),
                entry("wantToPlay", cbg.wantToPlay()),
                entry("wantToBuy", cbg.wantToBuy()),
                entry("wishlisted", cbg.wishlisted()),
                entry("preordered", cbg.preordered()),
                entry("played", cbg.played()),
                entry("numberOfPlays", cbg.numberOfPlays()),
                entry("publicComment", cbg.publicComment())
        );
    }

	private GeekBuddies geekBuddies() {
		return new GeekBuddiesBggInMemory();
	}
}
