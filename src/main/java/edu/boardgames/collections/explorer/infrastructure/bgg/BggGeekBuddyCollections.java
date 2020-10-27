package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.BoardGames;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollection;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollections;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.w3c.dom.Node;

import java.util.function.UnaryOperator;

public class BggGeekBuddyCollections implements GeekBuddyCollections {
	private final BoardGames boardGames;

	BggGeekBuddyCollections(BoardGames boardGames) {
		this.boardGames = boardGames;
	}

	@Override
	public BoardGameCollection owned(GeekBuddy geekBuddy) {
		return this.requestCollection(geekBuddy, CollectionRequest::owned);
	}

	@Override
	public BoardGameCollection wantToPlay(GeekBuddy geekBuddy) {
		return this.requestCollection(geekBuddy, CollectionRequest::wantToPlay);
	}

	@Override
	public BoardGameCollection minimallyRated(GeekBuddy geekBuddy, int minrating) {
		return this.requestCollection(geekBuddy, request -> request.minimallyRated(minrating));
	}

	public BoardGameCollection wantInTrade(GeekBuddy geekBuddy) {
		return this.requestCollection(geekBuddy, UnaryOperator.identity());
	}

	public BoardGameCollection wantToBuy(GeekBuddy geekBuddy) {
		return this.requestCollection(geekBuddy, UnaryOperator.identity());
	}

	public BoardGameCollection previouslyOwned(GeekBuddy geekBuddy) {
		return this.requestCollection(geekBuddy, UnaryOperator.identity());
	}

	public BoardGameCollection wishlist(GeekBuddy geekBuddy) {
		return this.requestCollection(geekBuddy, UnaryOperator.identity());
	}

	public BoardGameCollection rated(GeekBuddy geekBuddy) {
		return this.requestCollection(geekBuddy, UnaryOperator.identity());
	}

	public BoardGameCollection played(GeekBuddy geekBuddy) {
		//TODO_EDU with play stats?
		return this.requestCollection(geekBuddy, UnaryOperator.identity());
	}

	public BoardGameCollection preordered(GeekBuddy geekBuddy) {
		return this.requestCollection(geekBuddy, UnaryOperator.identity());
	}

	private BoardGameCollection requestCollection(GeekBuddy geekBuddy, UnaryOperator<CollectionRequest> processor) {
		CollectionRequest collectionRequest = processor.apply(new CollectionRequest(geekBuddy.username()).abbreviatedResults().withoutExpansions());
		return new GeekBuddyCollection(geekBuddy,
				boardGames.withIds(XmlNode.nodes(new XmlInput().read(collectionRequest.asInputStream()), "//item/@objectid").map(Node::getTextContent))
		);
	}
}
