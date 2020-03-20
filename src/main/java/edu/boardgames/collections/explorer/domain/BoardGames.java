package edu.boardgames.collections.explorer.domain;

import java.util.List;
import java.util.stream.Stream;

public interface BoardGames {
	List<BoardGame> withIds(Stream<String> ids);
}
