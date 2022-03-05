package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import edu.boardgames.collections.explorer.domain.Play;
import edu.boardgames.collections.explorer.infrastructure.Async;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlHttpRequest;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.w3c.dom.Node;

import static java.lang.System.Logger.Level.INFO;

public class PlaysEndpoint implements BggEndpoint {
    private static final System.Logger LOGGER = System.getLogger(PlaysEndpoint.class.getName());

    private final XmlHttpRequest bggRequest;
    private static final Page PAGING = new Page(100);

    public PlaysEndpoint() {
        this.bggRequest = new XmlHttpRequest(BggApi.V2.create("plays"))
                .addOption("type", "thing")
                .addOption("page", Integer.toString(1));
    }

    public PlaysEndpoint username(String username) {
        this.bggRequest.addOption("username", username);
        return this;
    }

    public PlaysEndpoint id(String id) {
        this.bggRequest.addOption("id", id);
        return this;
    }

    @Override
    public String asXml() {
        return this.bggRequest.asXml();
    }

    public Stream<Play> execute() {
        Node firstPage = this.bggRequest.asNode();
        int total = XmlNode.nodes(firstPage, "/plays")
                .findFirst()
                .map(TotalPlaysBggXml::new)
                .map(TotalPlaysBggXml::total)
                .orElse(0);
        int pageCount = PAGING.count(total);
        LOGGER.log(INFO, "Performing {0,number,integer} paged requests to fetch {1,number,integer} plays", pageCount, total);
        return Async.map(IntStream.rangeClosed(1, pageCount)
                                .mapToObj(Integer::toString)
                                .map(p -> this.bggRequest.copy().addOption("page", p)),
                        XmlHttpRequest::asNode)
                .flatMap(node -> XmlNode.nodes(node, "//play"))
                .map(PlayBggXml::new);
    }

    private static class TotalPlaysBggXml extends XmlNode {
        TotalPlaysBggXml(Node node) {
            super(node);
        }

        public int total() {
            return number("@total").intValue();
        }

        public int page() {
            return number("@page").intValue();
        }
    }
}
