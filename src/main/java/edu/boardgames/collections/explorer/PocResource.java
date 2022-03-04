package edu.boardgames.collections.explorer;

import java.lang.System.Logger.Level;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import edu.boardgames.collections.explorer.ui.input.BestWithInput;
import edu.boardgames.collections.explorer.ui.input.CollectionsInput;

@Path("/poc")
public class PocResource {
    private static final System.Logger LOGGER = System.getLogger(PocResource.class.getName());

    @GET
    @Path("/json/play")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<BoardGameJson> whatToPlay(@QueryParam("collections") String collections, @QueryParam("bestWith") Integer bestWith) {
        CollectionsInput searchableCollections = new CollectionsInput(collections);
        var bestWithInput = BestWithInput.of(bestWith);
        LOGGER.log(Level.INFO, "Search currently playable collections {0} to play a best with {1} game", searchableCollections.asText(), bestWithInput.asText());
        return searchableCollections.resolve().boardGames()
                .stream()
                .filter(bestWithInput.resolve())
                .distinct()
                .map(BoardGameJson::new)
                .toList();
    }
}
