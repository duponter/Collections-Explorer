package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.GeekList;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Objects;

public class GeekListBggXml extends XmlNode implements GeekList {
	private final String id;

	public GeekListBggXml(Node node) {
		super(node);
		this.id = id();
	}

	@Override
	public String id() {
		return string("@id");
	}

	@Override
	public String name() {
		return string("title");
	}

	@Override
	public List<BoardGame> boardGames() {
		return BggInit.get().boardGames().withIds(
				this.nodes("item[@subtype='boardgame']/@objectid")
					.map(Node::getTextContent)
		);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GeekListBggXml that = (GeekListBggXml) o;
		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
