package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.List;

import edu.boardgames.collections.explorer.domain.Play;
import edu.boardgames.collections.explorer.domain.Plays;

public class BggPlays implements Plays {
	@Override
	public List<Play> forUser(String username) {
        return new PlaysRequest().username(username).execute().toList();
	}

	@Override
	public List<Play> forUserAndGame(String username, String id) {
        return new PlaysRequest().username(username).id(id).execute().toList();
    }
}
