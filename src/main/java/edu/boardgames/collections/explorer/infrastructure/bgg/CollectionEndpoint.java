package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.net.http.HttpClient;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;

import edu.boardgames.collections.explorer.domain.CollectedBoardGame;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlHttpRequest;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;

public final class CollectionEndpoint implements BggEndpoint {
    private final XmlHttpRequest bggRequest;
    private final String username;

    public CollectionEndpoint(String username) {
        this(username, () -> HttpClient.newBuilder().build());
    }

    public CollectionEndpoint(String username, String password) {
        this(username, () -> new LoginRequest(username, password).send());
        this.bggRequest.enableOption("showprivate");
    }

    private CollectionEndpoint(String username, Supplier<HttpClient> httpClientSupplier) {
        this.bggRequest = new XmlHttpRequest(BggApi.V2.create("collection"), httpClientSupplier)
                .addOption("username", username)
                .addOption("subtype", "boardgame");
        this.username = username;
    }

    public CollectionEndpoint owned() {
        this.bggRequest.enableOption("own");
        return this;
    }

    public CollectionEndpoint preordered() {
        this.bggRequest.enableOption("preordered");
        return this;
    }

    public CollectionEndpoint wantToPlay() {
        this.bggRequest.enableOption("wanttoplay");
        return this;
    }

    public CollectionEndpoint played() {
        this.bggRequest.enableOption("played");
        return this;
    }

    public CollectionEndpoint rated() {
        this.bggRequest.enableOption("rated");
        return this;
    }

    public CollectionEndpoint abbreviatedResults() {
        this.bggRequest.enableOption("brief");
        return this;
    }

    public CollectionEndpoint withStats() {
        this.bggRequest.enableOption("stats");
        return this;
    }

    public CollectionEndpoint withoutExpansions() {
        this.bggRequest.addOption("excludesubtype", "boardgameexpansion");
        return this;
    }

    public CollectionEndpoint minimallyRated(int minrating) {
        Validate.inclusiveBetween(1, 10, minrating);
        this.bggRequest.addOption("minrating", Integer.toString(minrating));
        return this;
    }

    public Stream<CollectedBoardGame> execute() {
        return XmlNode.nodes(this.bggRequest.asNode(), "//item")
                .map(CollectedBoardGameBggXml::new)
                .map(bg -> new CollectedBoardGameCopy(this.username, bg));
    }

    public String asXml() {
        return this.bggRequest.asXml();
    }
}
