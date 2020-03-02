package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.net.http.HttpClient;
import java.util.function.Supplier;

public class CollectionRequest extends BggRequest<CollectionRequest> {
	public CollectionRequest(String username) {
		this(username, () -> HttpClient.newBuilder().build());
	}

	public CollectionRequest(String username, String password) {
		this(username, () -> new LoginRequest(username, password).send());
		this.enableOption("showprivate");
	}

	private CollectionRequest(String username, Supplier<HttpClient> httpClientSupplier) {
		super("collection", httpClientSupplier);
		this.addOption("username", username);
		this.addOption("subtype", "boardgame");
	}

	@Override
	CollectionRequest self() {
		return this;
	}

	public CollectionRequest owned() {
		return this.enableOption("own");
	}

	public CollectionRequest wantToPlay() {
		return this.enableOption("wanttoplay");
	}

	public CollectionRequest withStats() {
		return this.enableOption("stats");
	}

	public CollectionRequest withoutExpansions() {
		return this.addOption("excludesubtype", "boardgameexpansion");
	}
}
