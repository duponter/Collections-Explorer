package edu.boardgames.collections.explorer.domain;

/*
cleanup
. Move to CollectionsInput
. rename to playableCollections?
. remove unused methods in GeekBuddies + GeekLists
. how to get other type of collections? want-to-play
 */
public interface BoardGameCollections {
	BoardGameCollection all();

	BoardGameCollection withNames(String... names);
}
