package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.Objects;

import edu.boardgames.collections.explorer.domain.CollectedBoardGame;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.w3c.dom.Node;

public final class GeekListBoardGameBggXml extends XmlNode implements CollectedBoardGame {
    private final String id;

    public GeekListBoardGameBggXml(Node node) {
        super(node);
        this.id = id();
    }

    @Override
    public String collection() {
        return string("../@id");
    }

    @Override
    public String id() {
        return string("@objectid");
    }

    @Override
    public String name() {
        return string("@objectname");
    }

    @Override
    public String originalName() {
        return name();
    }

    @Override
    public String year() {
        return null;
    }

    @Override
    public Integer rating() {
        return null;
    }

    @Override
    public boolean owned() {
        return false;
    }

    @Override
    public boolean previouslyOwned() {
        return false;
    }

    @Override
    public boolean forTrade() {
        return false;
    }

    @Override
    public boolean wanted() {
        return false;
    }

    @Override
    public boolean wantToPlay() {
        return false;
    }

    @Override
    public boolean wantToBuy() {
        return false;
    }

    @Override
    public boolean wishlisted() {
        return false;
    }

    @Override
    public boolean preordered() {
        return false;
    }

    @Override
    public Integer numberOfPlays() {
        return null;
    }

    @Override
    public String publicComment() {
        return string("body");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeekListBoardGameBggXml that = (GeekListBoardGameBggXml) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
