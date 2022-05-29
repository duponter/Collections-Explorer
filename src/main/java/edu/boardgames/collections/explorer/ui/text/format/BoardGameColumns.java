package edu.boardgames.collections.explorer.ui.text.format;

import java.util.Objects;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.CollectedBoardGame;
import edu.boardgames.collections.explorer.domain.Range;
import edu.boardgames.collections.explorer.ui.text.Column;

public final class BoardGameColumns {
    private BoardGameColumns() {
        throw new UnsupportedOperationException("This static class cannot be instantiated.");
    }

    public static Column<PerspectivedBoardGame> title() {
        return new Column<>("Boardgame", 70, new Column.Formatted("%-70s").compose(b -> StringUtils.abbreviate(b.boardGame().name(), 70)));
    }

    public static Column<PerspectivedBoardGame> year() {
        return new Column<>("Year", 4, b -> b.boardGame().year());
    }

    public static Column<PerspectivedBoardGame> bggScore() {
        return new Column<>("BGG Score", 10, b -> Score.score10().fullString(b.boardGame().bggScore()));
    }

    public static Column<PerspectivedBoardGame> playerCount() {
        Function<BoardGame, Range<String>> communityPlayerCount = boardGame -> boardGame.bestWithPlayerCount()
            .or(boardGame::recommendedWithPlayerCount)
            .orElseGet(boardGame::playerCount);
        return new Column<>("Players", 11, b -> "%5s>%-4s".formatted(b.boardGame().playerCount().formatted(), communityPlayerCount.apply(b.boardGame()).formatted()));
    }

    public static Column<PerspectivedBoardGame> playtime() {
        Function<Range<String>, String> formattedRange = Range::formatted;
        return new Column<>("Playtime", 11, new Column.Formatted("%7s min").compose(formattedRange).compose(BoardGame::playtime).compose(PerspectivedBoardGame::boardGame));
    }

    public static Column<PerspectivedBoardGame> weight() {
        return new Column<>("Weight", 6, b -> Score.score5().fullString(b.boardGame().averageWeight()));
    }

    public static Column<PerspectivedBoardGame> rating() {
        return new Column<>("Rating", 2, b -> b.collectedBoardGame().played() ? Objects.toString(b.collectedBoardGame().rating(), "-") : "");
    }

    public static Column<PerspectivedBoardGame> owners() {
        return new Column<>("Owners", 40, new Column.Formatted("%-40s").compose(CollectedBoardGame::collection).compose(PerspectivedBoardGame::collectedBoardGame));
    }
}
