package edu.boardgames.collections.explorer.domain;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.list.ImmutableList;

public interface BoardGameCollection {
    String id();

    String name();

    ImmutableList<CollectedBoardGame> boardGames();

    default boolean contains(BoardGameSummary boardGameSummary) {
        return boardGames().collect(CollectedBoardGame::id).anySatisfyWith(String::equals, boardGameSummary.id());
    }

    default <T> RichIterable<T> map(Function2<CollectedBoardGame, String, T> mapper) {
        return boardGames().collect(bg -> mapper.apply(bg, name()));
    }

    default List<BoardGame> boardGamesDetailed() {
        return BggInit.get().boardGames().withIds(boardGames().stream().map(CollectedBoardGame::id));
    }

    default Stream<Copy> copyStream() {
        return this.boardGamesDetailed().stream().map(bg -> new Copy(bg, this));
    }

	default Map<BoardGame, Set<String>> copiesPerBoardGame() {
        return copyStream().collect(
                Collectors.groupingBy(
                        Copy::boardGame,
                        Collectors.mapping(
                                Copy::collection,
                                Collectors.mapping(BoardGameCollection::name, Collectors.toCollection(TreeSet::new))
                        )
                )
        );
    }
}
