package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollection;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollections;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.w3c.dom.Node;

import java.io.InputStream;
import java.util.List;

public class BggGeekBuddyCollections implements GeekBuddyCollections {
	@Override
	public BoardGameCollection owned(GeekBuddy geekBuddy) {
		return new GeekBuddyCollection(geekBuddy, fromInputStream(new CollectionRequest(geekBuddy.username()).owned().abbreviatedResults().withoutExpansions().asInputStream()));
	}

	@Override
	public BoardGameCollection wantToPlay(GeekBuddy geekBuddy) {
		return new GeekBuddyCollection(geekBuddy, fromInputStream(new CollectionRequest(geekBuddy.username()).wantToPlay().abbreviatedResults().withoutExpansions().asInputStream()));
	}

	private static List<BoardGame> fromInputStream(InputStream inputStream) {
		return BggInit.get().boardGames().withIds(
				XmlNode.nodes(new XmlInput().read(inputStream), "//item/@objectid").map(Node::getTextContent)
		);
	}
}
