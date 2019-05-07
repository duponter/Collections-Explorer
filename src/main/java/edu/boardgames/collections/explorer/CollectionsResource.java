package edu.boardgames.collections.explorer;

import edu.boardgames.collections.explorer.infrastructure.bgg.XmlNode;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.time.temporal.ChronoUnit;
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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
		return this.fetchBggUserCollection(BodyHandlers.ofLines(), username, password).body().collect(Collectors.joining());
	}

	private <T> HttpResponse<T> fetchBggUserCollection(BodyHandler<T> bodyHandler, String username, String password) {
		HttpClient httpClient = loginIntoBgg(username, password);

		HttpRequest request = HttpRequest.newBuilder()
//				.uri(URI.create(String.format("https://www.boardgamegeek.com/xmlapi2/collection?username=%s&subtype=boardgame&own=1&showprivate=1", username)))
				.uri(URI.create(String.format("https://www.boardgamegeek.com/xmlapi2/collection?username=%s&subtype=boardgame&excludesubtype=boardgameexpansion&own=1&showprivate=1", username)))
				.version(Version.HTTP_2)
				.GET()
				.build();

		RetryPolicy<HttpResponse<T>> retryPolicy = new RetryPolicy<HttpResponse<T>>()
				.abortIf(response -> response.statusCode() == 200)
				.handleResultIf(response -> response.statusCode() == 202)
				.withBackoff(1L, 100L, ChronoUnit.SECONDS)
				.withMaxRetries(5);

		return Failsafe.with(retryPolicy).get(() -> httpClient.send(request, bodyHandler));
	}

	private HttpClient loginIntoBgg(String username, String password) {
		CookieManager cm = new CookieManager();
		HttpClient httpClient = HttpClient.newBuilder().cookieHandler(cm).followRedirects(Redirect.NORMAL).build();

		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			return httpClient;
		}

//		http://tutorials.jenkov.com/java-cryptography/cipher.html
//		https://stackoverflow.com/a/43779197/1571325
//		https://stackoverflow.com/a/18228702/1571325

		HttpRequest login = HttpRequest.newBuilder()
				.uri(URI.create("http://boardgamegeek.com/login"))
				.version(Version.HTTP_2)
				.POST(BodyPublishers.ofString(String.format("username=%s&password=%s", username, password)))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.build();

		HttpResponse<String> loginResponse = Failsafe.with().get(() -> httpClient.send(login, BodyHandlers.ofString()));
		System.out.printf("Login ended with %s%n", loginResponse.statusCode());
		System.out.printf("Got cookies:%n");
		cm.getCookieStore().getCookies().forEach(System.out::println);
		return httpClient;
	}

	@GET
	@Path("/users")
	@Produces(MediaType.TEXT_PLAIN)
	public String infoByIds(@QueryParam("usernames") String usernames, @QueryParam("firstnames") String firstnames, @QueryParam("playercount]") Integer playercount, @QueryParam("maxtime") Integer maxtime) throws ExecutionException, InterruptedException {
//		https://www.callicoder.com/java-8-completablefuture-tutorial/
//		http://tabulator.info/
		List<CompletableFuture<Document>> documentFutures = Arrays.stream(StringUtils.split(usernames, ","))
				.map(username -> CompletableFuture.supplyAsync(() -> this.fetchBggUserCollection(BodyHandlers.ofInputStream(), username, null).body()))
				.map(inputstreamFuture -> inputstreamFuture.thenApply(inputStream -> {
					try {
						return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
					} catch (ParserConfigurationException | SAXException | IOException e) {
						throw new IllegalArgumentException(e);
					}
				}))
				.collect(Collectors.toList());

		CompletableFuture<Void> allFutures = CompletableFuture.allOf(documentFutures.toArray(new CompletableFuture[0]));
		CompletableFuture<List<String>> allCollectionsFuture = allFutures.thenApply(v -> documentFutures.stream()
				.map(CompletableFuture::join)
				.map(document -> XmlNode.nodes(document, "//item/@objectid").map(Node::getNodeValue).collect(Collectors.joining(",")))
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