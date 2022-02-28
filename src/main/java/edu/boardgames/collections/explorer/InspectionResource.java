package edu.boardgames.collections.explorer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import edu.boardgames.collections.explorer.infrastructure.bgg.ThingEndpoint;
import edu.boardgames.collections.explorer.infrastructure.xml.XslStylesheet;
import edu.boardgames.collections.explorer.ui.input.BoardGameIdInput;

@Path("/inspect")
public class InspectionResource {
    private static final XslStylesheet BOARDGAME_STYLESHEET = new XslStylesheet("/inspect/boardgame/xsl");

    @GET
    @Path("/boardgame/xsl")
    @Produces(MediaType.TEXT_XML)
    public String xsl() {
        return BOARDGAME_STYLESHEET.read();
    }

    @GET
    @Path("/boardgame/{ids}")
    @Produces(MediaType.TEXT_XML)
    public String inspectBoardgames(@PathParam("ids") String ids) {
        return new ThingEndpoint().forIds(new BoardGameIdInput(ids).resolve()).asXml();
    }

    @GET
    @Path("/boardgame/{ids}/formatted")
    @Produces(MediaType.TEXT_XML)
    public String formatInspectedBoardgames(@PathParam("ids") String ids) {
        return BOARDGAME_STYLESHEET.includeInXml(inspectBoardgames(ids));
    }
}
