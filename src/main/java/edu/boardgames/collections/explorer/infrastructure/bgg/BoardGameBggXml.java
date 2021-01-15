package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.NumberOfPlayers;
import edu.boardgames.collections.explorer.domain.Range;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import io.vavr.Lazy;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.collection.Stream;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BoardGameBggXml extends XmlNode implements BoardGame {
	private final String id;
	private final Lazy<Map<NumberOfPlayers, PlayerCountPoll>> playerCountVotes;

	public BoardGameBggXml(Node node) {
		super(node);
		this.id = id();
		this.playerCountVotes = Lazy.of(
				() -> Stream.ofAll(this.nodes("poll[@name='suggested_numplayers']/results/result")
				                       .map(PlayerCountVotesBggXml::new)
				                       .map(LazyPlayerCountVotes::new))
				            .groupBy(PlayerCountVotes::numberOfPlayers)
				            .mapValues(PlayerCountPoll::new)
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
	public Integer playerCountTotalVoteCount() {
		return number("poll[@name='suggested_numplayers']/@totalvotes").intValue();
	}

	@Override
	public Optional<PlayerCountPoll> playerCountVotes(int playerCount) {
		return this.playerCountVotes.get().get(new NumberOfPlayers(String.valueOf(playerCount))).toJavaOptional();
	}

	@Override
	public Optional<Range<String>> bestWithPlayerCount() {
		return pollResultAsRange(poll -> poll.result() == PlayerCountPollChoice.BEST);
	}

	@Override
	public Optional<Range<String>> recommendedWithPlayerCount() {
		return pollResultAsRange(poll -> poll.result() != PlayerCountPollChoice.NOT_RECOMMENDED);
	}

	private Optional<Range<String>> pollResultAsRange(Predicate<PlayerCountPoll> pollPredicate) {
		Set<NumberOfPlayers> filtered = this.playerCountVotes.get()
		                                                     .filterValues(pollPredicate)
		                                                     .keySet();
		return filtered.headOption()
		               .map(head -> new Range<>(head.value(), filtered.last().value()))
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
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		BoardGameBggXml that = (BoardGameBggXml) o;
		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
