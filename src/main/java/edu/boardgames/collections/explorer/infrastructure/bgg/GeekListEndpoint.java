package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.GeekListCollection;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlHttpRequest;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;

public final class GeekListEndpoint implements BggEndpoint {
    private final String geekListId;

    public GeekListEndpoint(String id) {
        this.geekListId = id;
    }

    public GeekListCollection execute() {
        return XmlNode.nodes(this.createRequest().asNode(), "//geeklist")
                .map(GeekListBggXml::new)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unable to find GeekList with id %s in BGG", geekListId)));
    }

    @Override
    public String asXml() {
        return this.createRequest().asXml();
    }

    private XmlHttpRequest createRequest() {
        return new XmlHttpRequest(BggApi.V1.create(String.format("geeklist/%s", geekListId)));
    }
}
