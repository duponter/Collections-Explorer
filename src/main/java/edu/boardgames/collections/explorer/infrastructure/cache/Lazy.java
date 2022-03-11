package edu.boardgames.collections.explorer.infrastructure.cache;

import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private volatile T value; // AtomicReference?
//  http://tutorials.jenkov.com/java-concurrency/volatile.html
//  http://tutorials.jenkov.com/java-util-concurrent/atomicreference.html

    Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public synchronized T get() {
        if (value == null) {
            value = supplier.get();
        }
        return value;
    }
}

