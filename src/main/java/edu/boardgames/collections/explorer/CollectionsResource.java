package edu.boardgames.collections.explorer;

import edu.boardgames.collections.explorer.infrastructure.bgg.CollectionRequest;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
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
	@Path("/users")
	@Produces(MediaType.TEXT_PLAIN)
	public String infoByIds(@QueryParam("usernames") String usernames, @QueryParam("firstnames") String firstnames, @QueryParam("playercount]") Integer playercount, @QueryParam("maxtime") Integer maxtime) throws ExecutionException, InterruptedException {
//		https://www.callicoder.com/java-8-completablefuture-tutorial/
//		http://tabulator.info/
		List<CompletableFuture<Node>> documentFutures = Arrays.stream(StringUtils.split(usernames, ","))
				.map(username -> CompletableFuture.supplyAsync(() -> new CollectionRequest(username).owned().withoutExpansions().asInputStream()))
				.map(inputstreamFuture -> inputstreamFuture.thenApply(new XmlInput()::read))
				.collect(Collectors.toList());

		CompletableFuture<Void> allFutures = CompletableFuture.allOf(documentFutures.toArray(new CompletableFuture[0]));
		CompletableFuture<List<String>> allCollectionsFuture = allFutures.thenApply(v -> documentFutures.stream()
				.map(CompletableFuture::join)
				.map(document -> XmlNode.nodes(document, "//item/name[@sortindex='1']/text()").map(Node::getNodeValue).collect(Collectors.joining(",")))
				.collect(Collectors.toList()));

		return "to implement via 1. CompletableFuture.allOf() and render via http://tabulator.info/\nids:\n"+ StringUtils.join(allCollectionsFuture.get(), "\n");
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