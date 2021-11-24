package edu.boardgames.collections.explorer.domain;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface BoardGameCollection {
	String id();

	String name();

	List<BoardGame> boardGames();

	default List<Copy> boardGameCopies() {
		return copyStream().toList();
	}

	default Stream<Copy> copyStream() {
		return this.boardGames().stream().map(bg -> new Copy(bg, this));
	}

	default Map<BoardGame, Set<String>> copiesPerBoardGame() {
		return groupByBoardGame(Collectors.mapping(Copy::collection, Collectors.mapping(BoardGameCollection::name, Collectors.toCollection(TreeSet::new))));
	}

	default <A, D> Map<BoardGame, D> groupByBoardGame(Collector<? super Copy, A, D> downstream) {
		return copyStream().collect(Collectors.groupingBy(Copy::boardGame, downstream));
	}
}
