package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.Range;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.w3c.dom.Node;

import java.util.Objects;
import java.util.Optional;

public class CollectionBoardGameBggXml extends XmlNode implements BoardGame {
	public CollectionBoardGameBggXml(Node node) {
		super(node);
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
	public Optional<Range<Integer>> bestWithPlayerCount() {
		return Optional.empty();
	}

	@Override
	public Optional<Range<Integer>> recommendedWithPlayerCount() {
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
		return name().equals(that.name()) &&
				year().equals(that.year());
	}

	@Override
	public int hashCode() {
		return Objects.hash(name(), year());
	}
}
