package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.Range;
import edu.boardgames.collections.explorer.domain.poll.NumberOfPlayers;
import edu.boardgames.collections.explorer.domain.poll.OldPlayerCountPoll;
import edu.boardgames.collections.explorer.domain.poll.PlayerCountPoll;
import edu.boardgames.collections.explorer.domain.poll.PlayerCountPollChoice;
import edu.boardgames.collections.explorer.domain.poll.PlayerCountVotes;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import io.vavr.Lazy;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.map.MutableMap;
import org.w3c.dom.Node;

public class BoardGameBggXml extends XmlNode implements BoardGame {
    private final String id;
    private final Lazy<MutableMap<NumberOfPlayers, OldPlayerCountPoll>> playerCountVotes;

    private final Lazy<PlayerCountPoll> playerCountPoll;

	public BoardGameBggXml(Node node) {
		super(node);
		this.id = id();
		this.playerCountVotes = Lazy.of(
                () -> Lists.immutable.fromStream(this.nodes("poll[@name='suggested_numplayers']/results/result")
                                .map(PlayerCountVotesBggXml::new))
                        .groupBy(PlayerCountVotes::numberOfPlayers)
                        .toMap()
                        .collectValues((playerCount, votes) -> new OldPlayerCountPoll(votes))
        );
        this.playerCountPoll = Lazy.of(
            () -> new PlayerCountPoll(
                new PlayerCountPollBggXml(
                    this.nodes("poll[@name='suggested_numplayers']").findFirst().orElseThrow(() -> new IllegalArgumentException("No Player Count Poll found for " + id()))
                )
            )
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
    public PlayerCountPoll playerCountPoll() {
        return playerCountPoll.get();
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
	public Optional<OldPlayerCountPoll> playerCountVotes(int playerCount) {
        return Optional.ofNullable(this.playerCountVotes.get().get(NumberOfPlayers.of(String.valueOf(playerCount))));
	}

	@Override
	public Optional<Range<String>> bestWithPlayerCount() {
		return pollResultAsRange(poll -> poll.result() == PlayerCountPollChoice.BEST);
	}

	@Override
	public Optional<Range<String>> recommendedWithPlayerCount() {
		return pollResultAsRange(poll -> poll.result() != PlayerCountPollChoice.NOT_RECOMMENDED);
	}

	private Optional<Range<String>> pollResultAsRange(Predicate<OldPlayerCountPoll> pollPredicate) {
        RichIterable<NumberOfPlayers> filtered = this.playerCountVotes.get().select((pc, poll) -> pollPredicate.test(poll)).keysView();
        return filtered.minOptional().map(min -> new Range<>(min.value(), filtered.max().value()));
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
	public List<BoardGame> contains() {
		return BggInit.get().boardGames()
				.withIds(
						this.nodes("link[@type='boardgamecompilation'][@inbound='true']/@id")
								.map(Node::getNodeValue)
				);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
        if (o instanceof BoardGame that) {
            return id.equals(that.id());
        }
        return false;
    }

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
