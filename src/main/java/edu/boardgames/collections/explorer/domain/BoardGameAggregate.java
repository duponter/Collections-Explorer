package edu.boardgames.collections.explorer.domain;

import java.util.Objects;

import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;
import io.vavr.Lazy;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

public final class BoardGameAggregate {
    private final ImmutableList<MutableCollectedBoardGame> collection;
    private final Lazy<ImmutableMap<String, BoardGame>> boardGames;

    public BoardGameAggregate(BoardGameCollection collection) {
        this(collection.boardGames().collect(MutableCollectedBoardGame::new));
    }

    public BoardGameAggregate(ImmutableList<MutableCollectedBoardGame> collection) {
        this(collection, boardGameMap(collection));
    }

    public BoardGameAggregate(ImmutableList<MutableCollectedBoardGame> collection, Lazy<ImmutableMap<String, BoardGame>> boardGames) {
        this.collection = Objects.requireNonNull(collection);
        this.boardGames = boardGames;
    }

    public BoardGameAggregate flatten(Function<RichIterable<MutableCollectedBoardGame>, MutableCollectedBoardGame> mapper) {
        return new BoardGameAggregate(collection.groupBy(MutableCollectedBoardGame::id).multiValuesView().collect(mapper).toImmutableList(), boardGames);
    }

    public BoardGameAggregate merge(BoardGameCollection collection, Function2<MutableCollectedBoardGame, CollectedBoardGame, MutableCollectedBoardGame> merger) {
        ImmutableMap<String, CollectedBoardGame> mapById = collection.boardGames().toImmutableMap(CollectedBoardGame::id, b -> b);
        return new BoardGameAggregate(this.collection.collect(mcbg -> mergeIfNotNull(mcbg, mapById.get(mcbg.id()), merger)), boardGames);
    }

    private MutableCollectedBoardGame mergeIfNotNull(MutableCollectedBoardGame mcbg, CollectedBoardGame cbg, Function2<MutableCollectedBoardGame, CollectedBoardGame, MutableCollectedBoardGame> merger) {
        return cbg != null ? merger.apply(mcbg, cbg) : mcbg;
    }

    public BoardGameAggregate filter(Predicate<? super CollectedBoardGame> filter) {
        return new BoardGameAggregate(this.collection.select(filter), boardGames);
    }

    public BoardGameAggregate filter(Predicate2<MutableCollectedBoardGame, BoardGame> filter) {
        return new BoardGameAggregate(this.collection.select(mcbg -> filter.accept(mcbg, boardGames.get().get(mcbg.id()))), boardGames);
    }

    public <T> RichIterable<T> map(Function<MutableCollectedBoardGame, T> mapper) {
        return collection.collect(mapper);
    }

    public <T> RichIterable<T> map(Function2<MutableCollectedBoardGame, BoardGame, T> mapper) {
        return collection.collect(mcbg -> mapper.apply(mcbg, boardGames.get().get(mcbg.id())));
    }

    private static Lazy<ImmutableMap<String, BoardGame>> boardGameMap(ImmutableList<MutableCollectedBoardGame> collection) {
        return Lazy.of(
            () -> Lists.immutable.withAll(
                BggInit.get().boardGames()
                    .withIds(Lists.immutable.withAll(collection).collect(CollectedBoardGame::id)
                        .stream())
            ).toImmutableMap(BoardGame::id, bg -> bg)
        );
    }
}
