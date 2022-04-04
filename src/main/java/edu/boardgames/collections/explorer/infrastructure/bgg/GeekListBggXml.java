package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.List;
import java.util.Objects;

import edu.boardgames.collections.explorer.domain.CollectedBoardGame;
import edu.boardgames.collections.explorer.domain.GeekListCollection;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.w3c.dom.Node;

public class GeekListBggXml extends XmlNode implements GeekListCollection {
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
    public List<CollectedBoardGame> boardGames() {
        return this.nodes("item")
                .map(GeekListBoardGameBggXml::new)
                .map(CollectedBoardGame.class::cast)
                .toList();
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
