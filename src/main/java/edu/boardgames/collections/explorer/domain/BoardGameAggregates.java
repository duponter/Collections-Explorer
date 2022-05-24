package edu.boardgames.collections.explorer.domain;

import java.util.function.Predicate;

import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.predicate.Predicate2;

public final class BoardGameAggregates {
    /**
     * Private constructor to prevent this static class from being instantiated.
     */
    private BoardGameAggregates() {
        throw new UnsupportedOperationException("This static class cannot be instantiated.");
    }

    public static MutableCollectedBoardGame joinCollectionNames(RichIterable<MutableCollectedBoardGame> group) {
        return new MutableCollectedBoardGame((BoardGameSummary) group.getAny())
            .collection(
                group.collect(MutableCollectedBoardGame::collection)
                    .collect(c -> BggInit.get().collections().one(c).name())
                    .makeString(", ")
            );
    }

    public static MutableCollectedBoardGame addsPlayedInfo(MutableCollectedBoardGame source, CollectedBoardGame added) {
        return new MutableCollectedBoardGame(source).numberOfPlays(added.numberOfPlays()).rating(added.rating());
    }

    public static MutableCollectedBoardGame toggleWantToPlay(MutableCollectedBoardGame source, CollectedBoardGame added) {
        return new MutableCollectedBoardGame(source).wantToPlay(true);
    }

    public static Predicate2<MutableCollectedBoardGame, BoardGame> collectedBoardGame(Predicate<? super MutableCollectedBoardGame> first) {
        return (m, bg) -> first.test(m);
    }

    public static Predicate2<MutableCollectedBoardGame, BoardGame> boardGame(Predicate<? super BoardGame> second) {
        return (m, bg) -> second.test(bg);
    }
}
