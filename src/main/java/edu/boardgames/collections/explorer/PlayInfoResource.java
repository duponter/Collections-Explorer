package edu.boardgames.collections.explorer;

import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;
import edu.boardgames.collections.explorer.infrastructure.bgg.PlaysRequest;
import edu.boardgames.collections.explorer.infrastructure.bgg.ThingRequest;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/playinfo")
public class PlayInfoResource {
	private static final Logger LOGGER = System.getLogger(PlayInfoResource.class.getName());

	@GET
	@Path("/xsl")
	@Produces(MediaType.TEXT_XML)
	public String xsl() throws URISyntaxException, IOException {
		return Files.readString(java.nio.file.Path.of(PlayInfoResource.class.getResource("play-info.xsl").toURI()));
	}

	@GET
	@Path("/formatted/{id}")
	@Produces(MediaType.TEXT_XML)
	public String xml(@PathParam("id") String id) {
		String response = new ThingRequest().withStats().forIds(Arrays.asList(id.split("\\s*,\\s*"))).asLines().collect(Collectors.joining());

		String xsl = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<?xml-stylesheet type=\"text/xsl\" href=\"http://localhost:8080/playinfo/xsl\"?>\n";
		String result = String.format("%s%n%s", xsl, StringUtils.removeStartIgnoreCase(response, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		LOGGER.log(Level.INFO, result);
		return result;
	}

    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String infoByIds(@PathParam("id") String id) {
		return BggInit.get().boardGames().withIds(Stream.of(id)).stream()
			    .map(BoardGameRender::playInfo)
			    .collect(Collectors.joining(System.lineSeparator()));
    }

	@GET
	@Path("/plays/{username}")
	@Produces(MediaType.TEXT_PLAIN)
	public String userPlays(@PathParam("username") String username) {
		return new PlaysRequest().username(username)
		                         .asInputStreams()
		                         .map(new XmlInput()::read)
		                         .flatMap(root -> XmlNode.nodes(root, "//play"))
		                         .map(node -> new XmlNode(node) {
			                         public String render() {
				                         return String.format("Id: %s - Date: %s - Location: %s", this.string("@id"), this.string("@date"), this.string("@location"));
			                         }
		                         })
		                         .map(xmlNode -> xmlNode.render())
		                         .sorted()
		                         .collect(Collectors.joining(System.lineSeparator()));
	}
}
