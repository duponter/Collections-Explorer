package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.List;
import java.util.stream.Stream;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.BoardGames;

import static java.lang.System.Logger.Level.INFO;

public class BggBoardGames implements BoardGames {
    private static final System.Logger LOGGER = System.getLogger(BggBoardGames.class.getName());

	@Override
	public List<BoardGame> withIds(Stream<String> ids) {
		List<String> boardGameIds = ids.toList();
        LOGGER.log(INFO, "Fetching {0,number,integer} boardgames by id", boardGameIds.size());
		return new ThingEndpoint().forIds(boardGameIds).execute().toList();
	}
}
