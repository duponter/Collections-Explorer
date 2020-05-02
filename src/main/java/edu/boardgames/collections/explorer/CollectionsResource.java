package edu.boardgames.collections.explorer;

import edu.boardgames.collections.explorer.domain.GeekBuddies;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.domain.GeekList;
import edu.boardgames.collections.explorer.domain.PlayerCount;
import edu.boardgames.collections.explorer.infrastructure.Async;
import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;
import edu.boardgames.collections.explorer.infrastructure.bgg.CollectionBoardGameBggXml;
import edu.boardgames.collections.explorer.infrastructure.bgg.CollectionRequest;
import edu.boardgames.collections.explorer.infrastructure.bgg.GeekBuddiesBggInMemory;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import io.reactivex.Flowable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/collections")
public class CollectionsResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionsResource.class);

	@GET
	@Path("/geeklist/{geeklist}")
	@Produces(MediaType.TEXT_PLAIN)
	public String geeklist(@PathParam("geeklist") String geeklistId, @QueryParam("bestWith") Integer bestWith) {
		LOGGER.info("Lookup boardgames in geeklist {} for best with {}", geeklistId, bestWith);
		GeekList geeklist = BggInit.get().geekLists().withId(geeklistId);

		String boardGames = geeklist.boardGames().stream()
				.filter(bestWith == null ? always -> true : new PlayerCount(bestWith)::recommendedOnly)
				.map(BoardGameRender::playInfo)
				.collect(Collectors.joining("\n"));
		return String.format("Search all board games for best with %d of geeklist %s%n%n%s", bestWith, geeklist.name(), boardGames);
	}

	@GET
	@Path("/{geekbuddy}/wanttoplay")
	@Produces(MediaType.TEXT_PLAIN)
	public String wantToPlay(@PathParam("geekbuddy") String geekbuddy, @QueryParam("bestWith") Integer bestWithFilter) {
		LOGGER.info("Search collections of all geekbuddies for best with {} want-to-play games of {}", bestWithFilter, geekbuddy);
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
		return new CollectionRequest(username, password).owned().asLines().collect(Collectors.joining());
	}

	@GET
	@Path("/stream")
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public Publisher<String> publishers() {
		Function<String, Pair<String, String>> toPair = username -> Pair.of(username, username);
		Function<String, InputStream> mapper = username -> new CollectionRequest(username).owned().withStats().withoutExpansions().asInputStream();
		Function<String, Stream<String>> nameStringExtractor = mapper.andThen(new XmlInput()::read)
				.andThen(document -> XmlNode.nodes(document, "//item").map(CollectionBoardGameBggXml::new).map(BoardGameRender::playInfo));

//		List<Flowable<Pair<String, Stream<String>>>> flowables = Async.mapToFutures(users().values().stream(), toPair.andThen(pair -> mapRight(pair, nameStringExtractor))).stream().map(Flowable::fromFuture).collect(Collectors.toList());
//		return Flowable
//				.concat(flowables)
//				.flatMap(pair -> Flowable.fromIterable(pair.getRight().map(game -> String.format("%s (%s)", game, pair.getLeft())).collect(Collectors.toList())));
		return Flowable.fromIterable(geekBuddies().all())
				.map(GeekBuddy::username)
				.map(username -> toPair.andThen(pair -> mapRight(pair, nameStringExtractor)).apply(username))
				.flatMap(pair -> Flowable.fromIterable(pair.getRight().map(game -> String.format("%s (%s)", game, pair.getLeft())).collect(Collectors.toList())));
	}

	@GET
	@Path("/users")
	@Produces(MediaType.TEXT_PLAIN)
	public String infoByIds(@QueryParam("usernames") String usernames, @QueryParam("firstnames") String firstnames, @QueryParam("playercount") Integer playercount, @QueryParam("maxtime") Integer maxtime) {
//		https://www.callicoder.com/java-8-completablefuture-tutorial/
//		http://tabulator.info/

		Function<String, Pair<String, String>> toPair = username -> Pair.of(username, username);
		Function<String, InputStream> mapper = username -> new CollectionRequest(username).owned().withStats().withoutExpansions().asInputStream();
		Function<String, Stream<String>> nameStringExtractor = mapper.andThen(new XmlInput()::read)
				.andThen(document -> XmlNode.nodes(document, "//item").map(CollectionBoardGameBggXml::new).map(BoardGameRender::playInfo));

		Set<String> collectedNames = Async.map(Arrays.stream(StringUtils.split(usernames, ",")), toPair.andThen(pair -> mapRight(pair, nameStringExtractor)))
				.flatMap(pair -> pair.getRight().map(game -> String.format("%s (%s)", game, pair.getLeft())))
				.collect(Collectors.toCollection(TreeSet::new));

		return "to implement via 1. CompletableFuture.allOf() and render via http://tabulator.info/\nids:\n" + StringUtils.join(collectedNames, "\n");
	}

	private static <T, U, R> Pair<T, R> mapRight(Pair<T, U> pair, Function<U, R> mapper) {
		return Pair.of(pair.getLeft(), mapper.apply(pair.getRight()));
	}

	private GeekBuddies geekBuddies() {
		return new GeekBuddiesBggInMemory();
	}
}
