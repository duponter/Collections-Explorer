package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.Range;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.w3c.dom.Node;

import java.util.Objects;
import java.util.Optional;

public class CollectionBoardGameBggXml extends XmlNode implements BoardGame {
	private final String id;

	public CollectionBoardGameBggXml(Node node) {
		super(node);
		this.id = id();
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
	public String year() {
		return string("yearpublished");
	}

	@Override
	public Double bggScore() {
		return numericValueAttribute("stats/rating/average").doubleValue();
	}

	@Override
	public Range<String> playerCount() {
		return Range.of(string("stats/@minplayers"), string("stats/@maxplayers"));
	}

	@Override
	public Optional<Range<String>> bestWithPlayerCount() {
		return Optional.empty();
	}

	@Override
	public Optional<Range<String>> recommendedWithPlayerCount() {
		return Optional.empty();
	}

	@Override
	public Range<String> playtime() {
		return Range.of(string("stats/@minplaytime"), string("stats/@maxplaytime"));
	}

	@Override
	public Double averageWeight() {
		return Double.NaN;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CollectionBoardGameBggXml that = (CollectionBoardGameBggXml) o;
		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
