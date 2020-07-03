package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.NumberOfPlayers;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.w3c.dom.Node;

public class PlayerCountVotesBggXml extends XmlNode implements PlayerCountVotes {
	protected PlayerCountVotesBggXml(Node node) {
		super(node);
	}

	@Override
	public NumberOfPlayers numberOfPlayers() {
		return new NumberOfPlayers(this.string("../@numplayers"));
	}

	@Override
	public String poll() {
		return this.string("@value");
	}

	public int voteCount() {
		return this.number("@numvotes").intValue();
	}
}
