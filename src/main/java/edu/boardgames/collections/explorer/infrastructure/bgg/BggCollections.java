package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.BoardGameCollections;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class BggCollections implements BoardGameCollections {
	@Override
	public BoardGameCollection owned(GeekBuddy geekBuddy) {
		return new BoardGameCollection(geekBuddy, fromInputStream(new CollectionRequest(geekBuddy.username()).owned().withStats().withoutExpansions().asInputStream()));
	}

	@Override
	public BoardGameCollection wantToPlay(GeekBuddy geekBuddy) {
		return new BoardGameCollection(geekBuddy, fromInputStream(new CollectionRequest(geekBuddy.username()).wantToPlay().withStats().withoutExpansions().asInputStream()));
	}

	private static List<BoardGame> fromInputStream(InputStream inputStream) {
		return XmlNode.nodes(new XmlInput().read(inputStream), "//item")
				.map(CollectionBoardGameBggXml::new)
				.collect(Collectors.toList());
	}
}
