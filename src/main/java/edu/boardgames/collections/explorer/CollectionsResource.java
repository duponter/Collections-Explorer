package edu.boardgames.collections.explorer;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.time.temporal.ChronoUnit;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
	public String xml(@PathParam("username") String username) throws URISyntaxException {
//		HttpRequest request = HttpRequest.newBuilder()
//				.uri(new URI("http://boardgamegeek.com/login?"))
//				.version(HttpClient.Version.HTTP_2)
//				.POST(BodyPublishers.noBody())
//				.build();
		/*
		POST http://boardgamegeek.com/login
		params: username - password
		store cookies
		https://www.baeldung.com/cookies-java

		https://boardgamegeek.com/thread/1112609/xml-api-private-info
		GET https://boardgamegeek.com/xmlapi2/collection?username=ForumMortsel&subtype=boardgame&own=1&showprivate=1
		&showprivate=1
		with stored cookies
		* */

		HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI(String.format("https://www.boardgamegeek.com/xmlapi2/collection?username=%s&subtype=boardgame&own=1&showprivate=1", username)))
				.version(Version.HTTP_2)
				.GET()
				.build();

		RetryPolicy<HttpResponse<String>> retryPolicy = new RetryPolicy<HttpResponse<String>>()
				.abortIf(response -> {
					System.out.printf("abortIf: %d%n", response.statusCode());
					return response.statusCode() == 200;
				})
				.handleResultIf(response -> {
					System.out.printf("handleResultIf: %d%n", response.statusCode());
					return response.statusCode() == 202;
				})
//				.abortIf((response,throwable) -> {
//					System.out.printf("status: %d%n", response.statusCode());
//					return response.statusCode() == 200;
//				})
				.onRetry(event -> System.out.printf("onRetry(%s)%n", event))
				.onAbort(event -> System.out.printf("onAbort(%s)%n", event))
				.onFailedAttempt(event -> System.out.printf("onFailedAttempt(%s)%n", event))
				.onRetriesExceeded(event -> System.out.printf("onRetriesExceeded(%s)%n", event))
				.onFailure(event -> System.out.printf("onFailure(%s)%n", event))
				.onSuccess(event -> System.out.printf("onSuccess(%s)%n", event))
				.withBackoff(1L, 100L, ChronoUnit.SECONDS)
				.withMaxRetries(5);

		System.out.printf("Allows Retries: %s%n", retryPolicy.allowsRetries());

		HttpResponse<String> response = Failsafe.with(retryPolicy).get(() -> HttpClient
				.newBuilder()
				.build()
				.send(request, BodyHandlers.ofString()));
		return response.body();
	}
}