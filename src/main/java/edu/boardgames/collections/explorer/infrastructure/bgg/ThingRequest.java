package edu.boardgames.collections.explorer.infrastructure.bgg;

public class ThingRequest extends BggRequest<ThingRequest> {
	public ThingRequest() {
		super("thing");
		this.addOption("type", "boardgame");
	}

	@Override
	ThingRequest self() {
		return this;
	}

	public ThingRequest forIds(String ids) {
		return this.addOption("id", ids);
	}

	public ThingRequest withStats() {
		return this.enableOption("stats");
	}
}
