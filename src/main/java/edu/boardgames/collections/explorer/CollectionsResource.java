package edu.boardgames.collections.explorer;

import edu.boardgames.collections.explorer.infrastructure.Async;
import edu.boardgames.collections.explorer.infrastructure.bgg.CollectionBoardGameBggXml;
import edu.boardgames.collections.explorer.infrastructure.bgg.CollectionRequest;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import io.reactivex.Flowable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.reactivestreams.Publisher;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
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
		return Flowable.fromIterable(users().values())
				.map(username -> toPair.andThen(pair -> mapRight(pair, nameStringExtractor)).apply(username))
				.flatMap(pair -> Flowable.fromIterable(pair.getRight().map(game -> String.format("%s (%s)", game, pair.getLeft())).collect(Collectors.toList())));
	}

	@GET
	@Path("/users")
	@Produces(MediaType.TEXT_PLAIN)
	public String infoByIds(@QueryParam("usernames") String usernames, @QueryParam("firstnames") String firstnames, @QueryParam("playercount]") Integer playercount, @QueryParam("maxtime") Integer maxtime) throws ExecutionException, InterruptedException {
//		https://www.callicoder.com/java-8-completablefuture-tutorial/
//		http://tabulator.info/

		Function<String, Pair<String, String>> toPair = username -> Pair.of(username, username);
		Function<String, InputStream> mapper = username -> new CollectionRequest(username).owned().withStats().withoutExpansions().asInputStream();
		Function<String, Stream<String>> nameStringExtractor = mapper.andThen(new XmlInput()::read)
				.andThen(document -> XmlNode.nodes(document, "//item").map(CollectionBoardGameBggXml::new).map(PlayInfoResource::playInfo));

		Set<String> collectedNames = Async.map(Arrays.stream(StringUtils.split(usernames, ",")), toPair.andThen(pair -> mapRight(pair, nameStringExtractor)))
				.flatMap(pair -> pair.getRight().map(game -> String.format("%s (%s)", game, pair.getLeft())))
				.collect(Collectors.toCollection(TreeSet::new));

		return "to implement via 1. CompletableFuture.allOf() and render via http://tabulator.info/\nids:\n" + StringUtils.join(collectedNames, "\n");
	}

	private static <T, U, R> Pair<T, R> mapRight(Pair<T, U> pair, Function<U, R> mapper) {
		return Pair.of(pair.getLeft(), mapper.apply(pair.getRight()));
	}

	private static Map<String, String> users() {
		return Map.ofEntries(
				Map.entry("Erwin", "duponter"),
				Map.entry("Koen", "jarrebesetoert"),
				Map.entry("Wouter", "WouterAerts"),
				Map.entry("Bart", "bartie"),
				Map.entry("Steffen", "de rode baron"),
				Map.entry("Edouard", "Edou"),
				Map.entry("Didier", "evildee"),
				Map.entry("Mortsel", "ForumMortsel"),
				Map.entry("Sven", "Svennos"),
				Map.entry("Dirk", "TurtleR6")
		);
	}
}