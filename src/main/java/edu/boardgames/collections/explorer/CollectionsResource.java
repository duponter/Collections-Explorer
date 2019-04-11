package edu.boardgames.collections.explorer;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

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
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.time.temporal.ChronoUnit;
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

	@GET
	@Path("/xsl")
	@Produces(MediaType.TEXT_XML)
	public String xsl() throws URISyntaxException, IOException {
		return Files.readString(java.nio.file.Path.of(CollectionsResource.class.getResource("play-info.xsl").toURI()));
	}

	@GET
	@Path("/xml/{username}")
	@Produces(MediaType.TEXT_PLAIN)
	public String xml(@PathParam("username") String username, @QueryParam("password") String password) throws URISyntaxException, IOException, InterruptedException {
		CookieManager cm = new CookieManager();
		HttpClient httpClient = HttpClient.newBuilder().cookieHandler(cm).followRedirects(Redirect.NORMAL).build();

//		http://tutorials.jenkov.com/java-cryptography/cipher.html
//		https://stackoverflow.com/a/43779197/1571325
//		https://stackoverflow.com/a/18228702/1571325

		HttpRequest login = HttpRequest.newBuilder()
				.uri(new URI("http://boardgamegeek.com/login"))
				.version(Version.HTTP_2)
				.POST(BodyPublishers.ofString(String.format("username=%s&password=%s", username, password)))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.build();

		HttpResponse<String> loginResponse = httpClient.send(login, BodyHandlers.ofString());
		System.out.printf("Login ended with %s%n", loginResponse.statusCode());
		System.out.printf("Got cookies:%n");
		cm.getCookieStore().getCookies().forEach(System.out::println);

		HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI(String.format("https://www.boardgamegeek.com/xmlapi2/collection?username=%s&subtype=boardgame&own=1&showprivate=1", username)))
				.version(Version.HTTP_2)
				.GET()
				.build();

		RetryPolicy<HttpResponse<Stream<String>>> retryPolicy = new RetryPolicy<HttpResponse<Stream<String>>>()
				.abortIf(response -> {
					System.out.printf("abortIf: %d%n", response.statusCode());
					return response.statusCode() == 200;
				})
				.handleResultIf(response -> {
					System.out.printf("handleResultIf: %d%n", response.statusCode());
					return response.statusCode() == 202;
				})
				.onRetry(event -> System.out.printf("onRetry(%s)%n", event))
				.onAbort(event -> System.out.printf("onAbort(%s)%n", event))
				.onFailedAttempt(event -> System.out.printf("onFailedAttempt(%s)%n", event))
				.onRetriesExceeded(event -> System.out.printf("onRetriesExceeded(%s)%n", event))
				.onFailure(event -> System.out.printf("onFailure(%s)%n", event))
				.onSuccess(event -> System.out.printf("onSuccess(%s)%n", event))
				.withBackoff(1L, 100L, ChronoUnit.SECONDS)
				.withMaxRetries(5);

		System.out.printf("Allows Retries: %s%n", retryPolicy.allowsRetries());

		HttpResponse<Stream<String>> response = Failsafe.with(retryPolicy).get(() -> httpClient.send(request, BodyHandlers.ofLines()));
		return response.body().filter(line -> line.contains("privateinfo")).collect(Collectors.joining());
	}
}