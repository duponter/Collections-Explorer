package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.Range;
import edu.boardgames.collections.explorer.domain.poll.PlayerCountPoll;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import io.vavr.Lazy;
import org.w3c.dom.Node;

public class BoardGameBggXml extends XmlNode implements BoardGame {
    private final String id;

    private final Lazy<PlayerCountPoll> playerCountPoll;

    public BoardGameBggXml(Node node) {
        super(node);
        this.id = id();
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
    public Optional<Range<String>> bestWithPlayerCount() {
        return playerCountPoll().bestOnly();
    }

    @Override
    public Optional<Range<String>> recommendedWithPlayerCount() {
        return playerCountPoll().recommended();
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
