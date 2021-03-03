package edu.boardgames.collections.explorer.domain;

public interface Player {
	String username();

	String name();

	boolean newPlayer();

	boolean won();

	double score();
}
