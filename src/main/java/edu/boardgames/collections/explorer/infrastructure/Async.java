package edu.boardgames.collections.explorer.infrastructure;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Async {
	/**
	 * Private constructor to prevent this static class from being instantiated.
	 */
	private Async() {
		throw new UnsupportedOperationException("This static class cannot be instantiated.");
	}

	public static <T, R> Stream<R> map(Stream<T> input, Function<T, R> mapper) {
		List<CompletableFuture<R>> operations = input
				.map(arg -> CompletableFuture.supplyAsync(() -> mapper.apply(arg)))
				.collect(Collectors.toList());

		return CompletableFuture.allOf(operations.toArray(new CompletableFuture[0]))
				.thenApply(v -> operations.stream().map(CompletableFuture::join))
				.join();
	}
}