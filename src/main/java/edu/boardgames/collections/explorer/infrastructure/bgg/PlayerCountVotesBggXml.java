package edu.boardgames.collections.explorer.infrastructure.bgg;

import org.w3c.dom.Node;

public class PlayerCountVotesBggXml extends XmlNode {
	protected PlayerCountVotesBggXml(Node node) {
		super(node);
	}

	public String value() {
		return this.string("@numplayers");
	}

	private Integer bestVotes() {
		return this.number("result[@value='Best']/@numvotes").intValue();
	}

	private Integer recommendedVotes() {
		return this.number("result[@value='Recommended']/@numvotes").intValue();
	}

	private Integer notRecommendedVotes() {
		return this.number("result[@value='Not Recommended']/@numvotes").intValue();
	}

	public PlayerCountPoll poll() {
		Integer best = this.bestVotes();
		Integer recommended = this.recommendedVotes();
		Integer notRecommended = this.notRecommendedVotes();

		if (best >= recommended && best >= notRecommended) {
			return PlayerCountPoll.BEST;
		} else if (recommended >= notRecommended) {
			return PlayerCountPoll.RECOMMENDED;
		} else {
			return PlayerCountPoll.NOT_RECOMMENDED;
		}
	}
}
