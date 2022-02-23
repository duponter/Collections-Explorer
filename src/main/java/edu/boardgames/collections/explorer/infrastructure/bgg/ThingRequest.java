package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.stream.Stream;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.infrastructure.Async;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlHttpRequest;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.eclipse.collections.api.factory.Lists;

public class ThingRequest {
    private static final Logger LOGGER = System.getLogger(ThingRequest.class.getName());

    /*
    Handle with https://jodah.net/failsafe/fallback/ ?
    414 Request-URI Too Large
    nginx/1.17.9
    max 8k = 8*1024 = 8192 chars

    min request= https://www.boardgamegeek.com/xmlapi2/thing?stats=1&id=&type=boardgame

    objectid = 6 chars
    separator = %2C

     */
    private final XmlHttpRequest bggRequest;
    private static final Page PAGING = new Page(900);
    private List<String> ids;

    public ThingRequest() {
        this.bggRequest = new XmlHttpRequest(BggApi.V2.create("thing"))
                .addOption("type", "boardgame")
                .enableOption("stats");
    }

    public ThingRequest forIds(List<String> ids) {
        this.ids = ids;
        return this;
    }

    public Stream<BoardGame> execute() {
        return Async.map(this.splitLargeRequests(), XmlHttpRequest::asNode)
                .flatMap(node -> XmlNode.nodes(node, "//item"))
                .map(BoardGameBggXml::new);
    }

    private Stream<XmlHttpRequest> splitLargeRequests() {
        if (ids.isEmpty()) {
            return Stream.empty();
        }
        int idCountPerRequest = PAGING.averageSize(ids.size());
        LOGGER.log(Level.INFO, "Performing %d requests for %d ids each".formatted(PAGING.count(ids.size()), idCountPerRequest));
        return Lists.immutable.withAll(ids)
                .chunk(idCountPerRequest)
                .collect(idsPerRequest -> this.bggRequest.copy().addOption("id", idsPerRequest.makeString(",")))
                .toList().stream();
    }

    public String asXml() {
        return this.bggRequest.asXml();
    }
}
