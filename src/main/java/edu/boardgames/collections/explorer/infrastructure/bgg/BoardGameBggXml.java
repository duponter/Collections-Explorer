package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.Range;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import io.vavr.Lazy;
import io.vavr.collection.Stream;
import org.w3c.dom.Node;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class BoardGameBggXml extends XmlNode implements BoardGame {
	private final String id;
	private final Lazy<Stream<PlayerCountVotesBggXml>> playerCountVotes;

	public BoardGameBggXml(Node node) {
		super(node);
		this.id = id();
		this.playerCountVotes = Lazy.of(() -> Stream.ofAll(this.nodes("poll[@name='suggested_numplayers']/results")
				.map(PlayerCountVotesBggXml::new)
				.sorted(Comparator.comparing(PlayerCountVotesBggXml::value)))
		);
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
		return new Range<>(stringValueAttribute("minplayers"), stringValueAttribute("maxplayers"));
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
		Stream<PlayerCountVotesBggXml> votes = this.playerCountVotes.get().filter(playerCountVotesBggXmlPredicate);
		return votes.headOption()
				.map(head -> new Range<>(head.value(), votes.last().value()))
				.toJavaOptional();
	}

	@Override
	public Range<String> playtime() {
		return new Range<>(stringValueAttribute("minplaytime"), stringValueAttribute("maxplaytime"));
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
