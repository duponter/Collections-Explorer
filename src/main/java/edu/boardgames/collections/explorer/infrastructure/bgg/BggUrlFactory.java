package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.net.URI;
import java.util.function.Function;

@FunctionalInterface
public interface BggUrlFactory extends Function<String, URI> {
	default URI create(String queryParameters) {
		return this.apply(queryParameters);
	}
}
