package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.poll.PlayerCountPollResult;
import edu.boardgames.collections.explorer.domain.poll.Poll;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.collector.Collectors2;
import org.w3c.dom.Node;

public class PlayerCountPollBggXml extends XmlNode implements Poll<PlayerCountPollResult> {
    public PlayerCountPollBggXml(Node node) {
        super(node);
    }

    @Override
    public String name() {
        return string("@name");
    }

    @Override
    public String title() {
        return string("@title");
    }

    @Override
    public int totalVotes() {
        return number("@totalvotes").intValue();
    }

    @Override
    public ImmutableList<PlayerCountPollResult> results() {
        return this.nodes("results/result")
            .map(PlayerCountPollResultBggXml::new)
            .collect(Collectors2.toImmutableSortedList());
    }
}
