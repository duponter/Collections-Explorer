package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.List;
import java.util.stream.Stream;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.BoardGames;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;

public class BggBoardGames implements BoardGames {
    private static final System.Logger LOGGER = System.getLogger(BggBoardGames.class.getName());

	@Override
	public List<BoardGame> withIds(Stream<String> ids) {
		List<String> boardGameIds = ids.toList();
        LOGGER.log(System.Logger.Level.INFO, "Fetching %d boardgames by id", boardGameIds.size());
		return new ThingRequest().withStats().forIds(boardGameIds).asInputStreams()
				.flatMap(is -> XmlNode.nodes(new XmlInput().read(is), "//item"))
				.map(BoardGameBggXml::new)
				.map(BoardGame.class::cast)
				.toList();
	}
}
