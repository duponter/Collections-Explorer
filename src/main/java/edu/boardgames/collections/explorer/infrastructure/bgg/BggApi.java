package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.net.URI;

public enum BggApi {
	V1("https://boardgamegeek.com/xmlapi/"),
	V2("https://boardgamegeek.com/xmlapi2/");

	private final String baseUrl;

	BggApi(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public BggUrlFactory create(String subpath) {
		return queryString -> URI.create("%s%s?%s".formatted(baseUrl, subpath, queryString));
	}
}
