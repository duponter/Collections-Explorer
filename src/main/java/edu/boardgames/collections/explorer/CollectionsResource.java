package edu.boardgames.collections.explorer;

import com.evanlennick.retry4j.CallExecutor;
import com.evanlennick.retry4j.CallExecutorBuilder;
import com.evanlennick.retry4j.Status;
import com.evanlennick.retry4j.config.RetryConfig;
import com.evanlennick.retry4j.config.RetryConfigBuilder;
import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.Range;
import edu.boardgames.collections.explorer.domain.bgg.BoardGameBggXml;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

@Path("/collections")
public class CollectionsResource {

	@GET
	@Path("/xsl")
	@Produces(MediaType.TEXT_XML)
	public String xsl() throws URISyntaxException, IOException {
		return Files.readString(java.nio.file.Path.of(CollectionsResource.class.getResource("play-info.xsl").toURI()));
	}

	@GET
	@Path("/xml")
	@Produces(MediaType.TEXT_PLAIN)
	public String xml() throws URISyntaxException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI("https://www.boardgamegeek.com/xmlapi2/collection?username=TurtleR6&subtype=boardgame&own=1"))
				.version(HttpClient.Version.HTTP_2)
				.GET()
				.build();

		RetryConfig config = new RetryConfigBuilder()
				.retryOnSpecificExceptions(IllegalStateException.class)
				.withMaxNumberOfTries(5)
				.withDelayBetweenTries(1L, ChronoUnit.SECONDS)
				.withExponentialBackoff()
				.build();

		Callable<String> callable = () -> {
			HttpResponse<String> response = HttpClient
					.newBuilder()
					.build()
					.send(request, BodyHandlers.ofString());
			if (response.statusCode() == 202) {
				throw new IllegalStateException("Accepted");
			}
			return response.body();
		};
		CallExecutor<String> executor = new CallExecutorBuilder().config(config)
				.afterFailedTryListener(status111 -> System.out.printf("afterFailedTryListener.onEvent(%s) called%n%n", status111))
				.beforeNextTryListener(status11 -> System.out.printf("beforeNextTryListener.onEvent(%s) called%n%n", status11))
				.onCompletionListener(status11111 -> System.out.printf("onCompletionListener.onEvent(%s) called%n%n", status11111))
				.onSuccessListener(status1111 -> System.out.printf("onSuccessListener.onEvent(%s) called%n%n", status1111))
				.onFailureListener(status1 -> System.out.printf("onFailureListener.onEvent(%s) called%n%n", status1))
				.build();
		Status<String> status = executor.execute(callable);


		return status.getResult();
	}

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() throws IOException, InterruptedException, URISyntaxException, ParserConfigurationException, SAXException {

	    HttpRequest request = HttpRequest.newBuilder()
			    .uri(new URI("https://www.boardgamegeek.com/xmlapi2/thing?type=boardgame&id=249703,245931"))
			    .version(HttpClient.Version.HTTP_2)
			    .GET()
			    .build();

	    HttpResponse<InputStream> response = HttpClient
			    .newBuilder()
			    .build().send(request, BodyHandlers.ofInputStream());


	    System.out.println(response.statusCode());
	    System.out.println(response.body());

	    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    Document document = builder.parse(response.body());
	    return playInfo(new BoardGameBggXml(document));
    }

	private static String playInfo(BoardGame boardGame) {
		Range<Integer> communityPlayerCount = boardGame.bestWithPlayerCount()
				.or(boardGame::recommendedWithPlayerCount)
				.orElse(boardGame.playerCount().map(Integer::parseInt));

		return String.format("%s (%s) - %s>%sp - %s Min", boardGame.name(), boardGame.year(), boardGame.playerCount().formatted(), communityPlayerCount.formatted(), boardGame.playtime().formatted());
	}
}