package edu.boardgames.collections.explorer.domain;

public interface BoardGameRepository {
	BoardGame fetchByIds(String... id);
}
