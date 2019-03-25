package edu.boardgames.collections.explorer;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.Range;
import edu.boardgames.collections.explorer.domain.bgg.BoardGameBggXml;
import org.apache.commons.lang3.StringUtils;
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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

@Path("/playinfo")
public class PlayInfoResource {

	@GET
	@Path("/xsl")
	@Produces(MediaType.TEXT_XML)
	public String xsl() throws URISyntaxException, IOException {
		return Files.readString(java.nio.file.Path.of(PlayInfoResource.class.getResource("play-info.xsl").toURI()));
	}

	@GET
	@Path("/xml")
	@Produces(MediaType.TEXT_XML)
	public String xml() throws URISyntaxException, IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI("https://www.boardgamegeek.com/xmlapi2/thing?type=boardgame&id=249703,245931"))
				.version(HttpClient.Version.HTTP_2)
				.GET()
				.build();

		HttpResponse<String> response = HttpClient
				.newBuilder()
				.build().send(request, BodyHandlers.ofString());

		String xsl = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<?xml-stylesheet type=\"text/xsl\" href=\"http://localhost:8080/playinfo/xsl\"?>\n";
		String result = String.format("%s%n%s", xsl, StringUtils.removeStartIgnoreCase(response.body(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		System.out.printf("%s%n", result);
		return result;
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