package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.BoardGames;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BggBoardGames implements BoardGames {
	@Override
	public List<BoardGame> withIds(Stream<String> ids) {
		return XmlNode.nodes(new XmlInput().read(new ThingRequest().withStats().forIds(ids.collect(Collectors.joining(","))).asInputStream()), "//item")
				.map(BoardGameBggXml::new)
				.collect(Collectors.toList());
	}
}
