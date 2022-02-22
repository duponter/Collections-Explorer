package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.GeekList;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;

public final class GeekListRequest {
    private final String geekListId;

    public GeekListRequest(String id) {
        this.geekListId = id;
    }

    public GeekList execute() {
        return XmlNode.nodes(new GenericBggRequest(BggApi.V1.create(String.format("geeklist/%s", geekListId))).asNode(), "//geeklist")
                .map(GeekListBggXml::new)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unable to find GeekList with id %s in BGG", geekListId)));
    }
}
