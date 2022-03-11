package edu.boardgames.collections.explorer.domain;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface BoardGameCollection {
	String id();

	String name();

	List<BoardGame> boardGames();

    default Stream<Copy> copyStream() {
        return this.boardGames().stream().map(bg -> new Copy(bg, this));
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
