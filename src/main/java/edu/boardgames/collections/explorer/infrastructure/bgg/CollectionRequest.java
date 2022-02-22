package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.net.http.HttpClient;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;

import edu.boardgames.collections.explorer.domain.CollectedBoardGame;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;

public final class CollectionRequest {
    private final GenericBggRequest bggRequest;

    public CollectionRequest(String username) {
        this(username, () -> HttpClient.newBuilder().build());
    }

    public CollectionRequest(String username, String password) {
        this(username, () -> new LoginRequest(username, password).send());
        this.bggRequest.enableOption("showprivate");
    }

    private CollectionRequest(String username, Supplier<HttpClient> httpClientSupplier) {
        this.bggRequest = new GenericBggRequest(BggApi.V2.create("collection"), httpClientSupplier)
                .addOption("username", username)
                .addOption("subtype", "boardgame");
    }

    public CollectionRequest owned() {
        this.bggRequest.enableOption("own");
        return this;
    }

    public CollectionRequest wantToPlay() {
        this.bggRequest.enableOption("wanttoplay");
        return this;
    }

    public CollectionRequest abbreviatedResults() {
        this.bggRequest.enableOption("brief");
        return this;
    }

    public CollectionRequest withStats() {
        this.bggRequest.enableOption("stats");
        return this;
    }

    public CollectionRequest withoutExpansions() {
        this.bggRequest.addOption("excludesubtype", "boardgameexpansion");
        return this;
    }

    public CollectionRequest minimallyRated(int minrating) {
        Validate.inclusiveBetween(1, 10, minrating);
        this.bggRequest.addOption("minrating", Integer.toString(minrating));
        return this;
    }

    public Stream<CollectedBoardGame> execute() {
        return XmlNode.nodes(this.bggRequest.asNode(), "//item")
                .map(CollectedBoardGameBggXml::new);
    }

    public String asXml() {
        return this.bggRequest.asXml();
    }
}
