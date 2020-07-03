package edu.boardgames.collections.explorer.infrastructure.bgg;

public enum PlayerCountPollChoice {
	BEST("Best"),
	RECOMMENDED("Recommended"),
	NOT_RECOMMENDED("Not Recommended");

	private final String value;

	PlayerCountPollChoice(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}
}
