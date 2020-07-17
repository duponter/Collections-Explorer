package edu.boardgames.collections.explorer.domain;

public interface BoardGameCollections {
	BoardGameCollection all();

	BoardGameCollection one(String name);

	BoardGameCollection withNames(String... names);
}
