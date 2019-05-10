package edu.boardgames.collections.explorer;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.Range;
import edu.boardgames.collections.explorer.infrastructure.bgg.BoardGameBggXml;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/playinfo")
public class PlayInfoResource {

	@GET
	@Path("/xsl")
	@Produces(MediaType.TEXT_XML)
	public String xsl() throws URISyntaxException, IOException {
		return Files.readString(java.nio.file.Path.of(PlayInfoResource.class.getResource("play-info.xsl").toURI()));
	}

	@GET
	@Path("/formatted/{id}")
	@Produces(MediaType.TEXT_XML)
	public String xml(@PathParam("id") String id) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://www.boardgamegeek.com/xmlapi2/thing?type=boardgame&stats=1&id=" + id))
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
    @Path("/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String infoByIds(@PathParam("id") String id) {
	    return XmlNode.nodes(new XmlInput().read(URI.create("https://www.boardgamegeek.com/xmlapi2/thing?type=boardgame&stats=1&id=" + id)), "//item")
			    .map(BoardGameBggXml::new)
			    .map(PlayInfoResource::playInfo)
			    .collect(Collectors.joining(System.lineSeparator()));
    }

	private static String playInfo(BoardGame boardGame) {
		Range<Integer> communityPlayerCount = boardGame.bestWithPlayerCount()
				.or(boardGame::recommendedWithPlayerCount)
				.orElse(boardGame.playerCount().map(Integer::parseInt));

		return String.format("%s (%s) - %s>%sp - %s Min - %2.1f / 10 - %1.2f / 5", boardGame.name(), boardGame.year(), boardGame.playerCount().formatted(), communityPlayerCount.formatted(), boardGame.playtime().formatted(), boardGame.bggScore(), boardGame.averageWeight());
	}
}