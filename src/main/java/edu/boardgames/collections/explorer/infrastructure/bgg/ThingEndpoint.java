package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlHttpRequest;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.eclipse.collections.api.factory.Lists;
import org.w3c.dom.Node;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.stream.Stream;

public class ThingEndpoint implements BggEndpoint {
    private static final Logger LOGGER = System.getLogger(ThingEndpoint.class.getName());

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
    private static final Page PAGING = new Page(20);
    private List<String> ids;

    public ThingEndpoint() {
        this.bggRequest = new XmlHttpRequest(BggApi.V2.create("thing"))
            .addOption("type", "boardgame")
            .enableOption("stats");
    }

    public ThingEndpoint forIds(List<String> ids) {
        this.ids = ids;
        return this;
    }

    public Stream<BoardGame> execute() {
        return execute(this.splitLargeRequests());
    }

    private Stream<BoardGame> execute(List<XmlHttpRequest> requests) {
        if (requests.isEmpty()) {
            return Stream.empty();
        }
        List<XmlHttpRequest> failedRequests = Lists.mutable.of();
        return Stream.concat(
            requests.parallelStream().flatMap(req -> {
                    List<Node> nodes = XmlNode.nodes(req.asNode(), "//item").toList();
                    if (nodes.isEmpty()) {
                        failedRequests.add(req);
                    }
                    return nodes.stream();
                })
                .map(BoardGameBggXml::new),
            execute(failedRequests)
        );
    }

    private List<XmlHttpRequest> splitLargeRequests() {
        if (ids.isEmpty()) {
            return List.of();
        }
        LOGGER.log(Level.INFO, "Performing %d requests for %d ids each".formatted(PAGING.count(ids.size()), PAGING.size()));
        return Lists.immutable.withAll(ids)
            .chunk(PAGING.size())
            .collect(idsPerRequest -> this.bggRequest.copy().addOption("id", idsPerRequest.makeString(",")))
            .toList();
    }

    public String asXml() {
        this.bggRequest.addOption("id", String.join(",", ids));
        return this.bggRequest.asXml();
    }
}
