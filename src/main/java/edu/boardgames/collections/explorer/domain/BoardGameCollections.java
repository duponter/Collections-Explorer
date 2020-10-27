package edu.boardgames.collections.explorer.domain;

/*
cleanup
. rename to playableCollections?
. remove unused methods in GeekBuddies + GeekLists
. how to get other type of collections? want-to-play
 */
public interface BoardGameCollections {
	BoardGameCollection all();

	BoardGameCollection one(String name);

	BoardGameCollection withNames(String... names);
}
