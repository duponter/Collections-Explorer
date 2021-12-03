package edu.boardgames.collections.explorer.ui.input;

import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;

public class GeekbuddyInput implements Input<GeekBuddy> {
	private final GeekBuddy geekBuddy;

	public GeekbuddyInput(String username) {
		this.geekBuddy = BggInit.get().geekBuddies().one(username);
	}

	@Override
	public String asText() {
		return this.geekBuddy.name();
	}

	@Override
	public GeekBuddy resolve() {
		return this.geekBuddy;
	}
}
