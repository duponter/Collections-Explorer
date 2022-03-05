package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.poll.PlayerCountPollResult;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.w3c.dom.Node;

public class PlayerCountPollResultBggXml extends XmlNode implements PlayerCountPollResult {
    protected PlayerCountPollResultBggXml(Node node) {
        super(node);
    }

    @Override
    public String numberOfPlayers() {
        return this.string("../@numplayers");
    }

    @Override
    public String value() {
        return this.string("@value");
    }

    @Override
    public int votes() {
        return this.number("@numvotes").intValue();
    }
}
