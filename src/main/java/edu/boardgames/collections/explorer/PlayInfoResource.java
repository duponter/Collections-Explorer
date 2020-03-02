package edu.boardgames.collections.explorer;

import edu.boardgames.collections.explorer.infrastructure.bgg.BoardGameBggXml;
import edu.boardgames.collections.explorer.infrastructure.bgg.ThingRequest;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
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
	public String xml(@PathParam("id") String id) {
		String response = new ThingRequest().withStats().forIds(id).asLines().collect(Collectors.joining());

		String xsl = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<?xml-stylesheet type=\"text/xsl\" href=\"http://localhost:8080/playinfo/xsl\"?>\n";
		String result = String.format("%s%n%s", xsl, StringUtils.removeStartIgnoreCase(response, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		System.out.printf("%s%n", result);
		return result;
	}

    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String infoByIds(@PathParam("id") String id) {
	    return XmlNode.nodes(new XmlInput().read(new ThingRequest().withStats().forIds(id).asInputStream()), "//item")
			    .map(BoardGameBggXml::new)
			    .map(BoardGameRender::playInfo)
			    .collect(Collectors.joining(System.lineSeparator()));
    }
}