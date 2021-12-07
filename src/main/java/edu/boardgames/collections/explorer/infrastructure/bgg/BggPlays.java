package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.List;

import edu.boardgames.collections.explorer.domain.Play;
import edu.boardgames.collections.explorer.domain.Plays;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BggPlays implements Plays {
	private static final Logger LOGGER = LoggerFactory.getLogger(BggPlays.class.getName());

	@Override
	public List<Play> forUser(String username) {
		LOGGER.info("Fetching all plays by {}", username);
		return this.executeRequest(new PlaysRequest().username(username));
	}

	@Override
	public List<Play> forUserAndGame(String username, String id) {
		LOGGER.info("Fetching first page of {} plays by {}", id, username);
		List<Play> plays = this.executeRequest(new PlaysRequest().username(username).id(id).page(1));
		if (plays.size() == 100) {
			LOGGER.warn("Exactly 100 records fetched because request is limited to first page of 100 records, requerying without page");
			plays = this.executeRequest(new PlaysRequest().username(username).id(id).page(0));
		}
		return plays;
	}

	private List<Play> executeRequest(PlaysRequest request) {
		List<Play> plays = request
				.asInputStreams()
				.map(new XmlInput()::read)
				.flatMap(root -> XmlNode.nodes(root, "//play"))
				.map(PlayBggXml::new)
				.map(Play.class::cast)
				.toList();
		LOGGER.info("Fetching {} logged plays", plays.size());
		return plays;
	}
}
