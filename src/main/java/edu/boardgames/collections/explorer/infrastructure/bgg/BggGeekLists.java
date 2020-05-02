package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.GeekList;
import edu.boardgames.collections.explorer.domain.GeekLists;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;

public class BggGeekLists implements GeekLists {
	@Override
	public GeekList withId(String id) {
		return XmlNode.nodes(new XmlInput().read(new GeekListRequest(id).asInputStream()), "//geeklist")
				.map(GeekListBggXml::new)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(String.format("Unable to find GeekList with id %s in BGG", id)));
	}
}
