package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.List;
import java.util.stream.Stream;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.BoardGames;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BggBoardGames implements BoardGames {
	private static final Logger LOGGER = LoggerFactory.getLogger(BggBoardGames.class.getName());

	@Override
	public List<BoardGame> withIds(Stream<String> ids) {
		List<String> boardGameIds = ids.toList();
		LOGGER.info("Fetching {} boardgames by id", boardGameIds.size());
		return new ThingRequest().withStats().forIds(boardGameIds).asInputStreams()
				.flatMap(is -> XmlNode.nodes(new XmlInput().read(is), "//item"))
				.map(BoardGameBggXml::new)
				.map(BoardGame.class::cast)
				.toList();
	}
}
