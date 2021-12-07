package edu.boardgames.collections.explorer.domain;

import java.util.List;

public interface Plays {
	List<Play> forUser(String username);

	List<Play> forUserAndGame(String username, String id);
}
