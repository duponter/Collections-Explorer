package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import edu.boardgames.collections.explorer.domain.CollectedBoardGame;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.w3c.dom.Node;

public final class CollectedBoardGameBggXml extends XmlNode implements CollectedBoardGame {
    private final String id;

    public CollectedBoardGameBggXml(Node node) {
        super(node);
        this.id = id();
    }

    @Override
    public String collection() {
        throw new UnsupportedOperationException("CollectedBoardGameBggXml does not support collection()");
    }

    @Override
    public String id() {
        return string("@objectid");
    }

    @Override
    public String name() {
        return string("name[@sortindex]");
    }

    @Override
    public String originalName() {
        return StringUtils.defaultIfEmpty(string("originalname"), name());
    }

    @Override
    public String year() {
        return string("yearpublished");
    }

    @Override
    public Integer rating() {
        String value = stringValueAttribute("stats/rating");
        return !StringUtils.equalsIgnoreCase(value, "N/A") ? Integer.parseInt(value) : null;
    }

    @Override
    public boolean owned() {
        return toBoolean("status/@own");
    }

    @Override
    public boolean previouslyOwned() {
        return toBoolean("status/@prevowned");
    }

    @Override
    public boolean forTrade() {
        return toBoolean("status/@fortrade");
    }

    @Override
    public boolean wanted() {
        return toBoolean("status/@want");
    }

    @Override
    public boolean wantToPlay() {
        return toBoolean("status/@wanttoplay");
    }

    @Override
    public boolean wantToBuy() {
        return toBoolean("status/@wanttobuy");
    }

    @Override
    public boolean wishlisted() {
        return toBoolean("status/@wishlist");
    }

    @Override
    public boolean preordered() {
        return toBoolean("status/@preordered");
    }

    @Override
    public Integer numberOfPlays() {
        return number("numplays").intValue();
    }

    @Override
    public String publicComment() {
        return string("comment");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectedBoardGameBggXml that = (CollectedBoardGameBggXml) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
