package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.Range;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BoardGameBggXml extends XmlNode implements BoardGame {
	private final String id;

	public BoardGameBggXml(Node node) {
		super(node);
		this.id = id();
	}

	@Override
	public String id() {
		return string("@id");
	}

	@Override
	public String name() {
		return stringValueAttribute("name[@type='primary']");
	}

	@Override
	public String year() {
		return stringValueAttribute("yearpublished");
	}

	@Override
	public Double bggScore() {
		return numericValueAttribute("statistics/ratings/average").doubleValue();
	}

	@Override
	public Range<String> playerCount() {
		return Range.of(stringValueAttribute("minplayers"), stringValueAttribute("maxplayers"));
	}

	@Override
	public Optional<Range<String>> bestWithPlayerCount() {
		return playerCountAsRange(votes -> votes.poll() == PlayerCountPoll.BEST);
	}

	@Override
	public Optional<Range<String>> recommendedWithPlayerCount() {
		return playerCountAsRange(votes -> votes.poll() != PlayerCountPoll.NOT_RECOMMENDED);
	}

	private Optional<Range<String>> playerCountAsRange(Predicate<PlayerCountVotesBggXml> playerCountVotesBggXmlPredicate) {
		List<String> playerCount = this.nodes("poll[@name='suggested_numplayers']/results")
				.map(PlayerCountVotesBggXml::new)
				.filter(playerCountVotesBggXmlPredicate)
				.map(PlayerCountVotesBggXml::value)
				.collect(Collectors.toList());

		switch (playerCount.size()) {
			case 0:
				return Optional.empty();
			case 1:
				return Optional.of(Range.of(playerCount.get(0), playerCount.get(0)));
			default:
				return Optional.of(Range.of(playerCount.get(0), playerCount.get(playerCount.size() - 1)));
		}
	}

	@Override
	public Range<String> playtime() {
		return Range.of(stringValueAttribute("minplaytime"), stringValueAttribute("maxplaytime"));
	}

	@Override
	public Double averageWeight() {
		return numericValueAttribute("statistics/ratings/averageweight").doubleValue();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BoardGameBggXml that = (BoardGameBggXml) o;
		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
