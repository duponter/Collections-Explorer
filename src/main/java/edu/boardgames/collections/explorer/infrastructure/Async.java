package edu.boardgames.collections.explorer.infrastructure;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
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
        return input.map(arg -> CompletableFuture.supplyAsync(() -> mapper.apply(arg)))
            .collect(Collectors.collectingAndThen(Collectors.toList(), Async::joinOperations));
    }

    public static <T, R> Stream<R> map(Stream<T> input, Function<T, R> mapper, int maxConcurrent) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(maxConcurrent)) {
            Semaphore semaphore = new Semaphore(maxConcurrent);

            List<CompletableFuture<R>> operations = input
                .map(arg -> {
                    semaphore.acquireUninterruptibly();
                    return CompletableFuture.supplyAsync(() -> {
                        try {
                            return mapper.apply(arg);
                        } finally {
                            semaphore.release();
                        }
                    }, executorService);
                })
                .toList();

            return joinOperations(operations);
        }
    }

    private static <R> Stream<R> joinOperations(List<CompletableFuture<R>> operations) {
        return CompletableFuture.allOf(operations.toArray(new CompletableFuture[0]))
            .thenApply(_ -> operations.stream().map(CompletableFuture::join))
            .join();
    }
}
