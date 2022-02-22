package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.List;

import edu.boardgames.collections.explorer.domain.Play;
import edu.boardgames.collections.explorer.domain.Plays;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;

import static java.lang.System.Logger.Level.INFO;
import static java.lang.System.Logger.Level.WARNING;

public class BggPlays implements Plays {
    private static final System.Logger LOGGER = System.getLogger(BggPlays.class.getName());

	@Override
	public List<Play> forUser(String username) {
        LOGGER.log(INFO, "Fetching all plays by %s", username);
		return this.executeRequest(new PlaysRequest().username(username));
	}

	@Override
	public List<Play> forUserAndGame(String username, String id) {
        LOGGER.log(INFO, "Fetching first page of %s plays by %s", id, username);
        // this is functionality for PlaysRequest
        List<Play> plays = this.executeRequest(new PlaysRequest().username(username).id(id).page(1));
        if (plays.size() == 100) {
            LOGGER.log(WARNING, "Exactly 100 records fetched because request is limited to first page of 100 records, requerying without page");
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
        LOGGER.log(INFO, "Fetching %d logged plays", plays.size());
		return plays;
	}
}
