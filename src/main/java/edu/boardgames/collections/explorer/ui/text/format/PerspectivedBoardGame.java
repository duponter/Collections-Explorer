package edu.boardgames.collections.explorer.ui.text.format;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.CollectedBoardGame;

public interface PerspectivedBoardGame {
    CollectedBoardGame collectedBoardGame();

    BoardGame boardGame();
}
