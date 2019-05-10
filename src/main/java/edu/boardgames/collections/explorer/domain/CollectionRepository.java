package edu.boardgames.collections.explorer.domain;

public interface CollectionRepository {
	BoardGameCollection fetchByUser(String username);
}
