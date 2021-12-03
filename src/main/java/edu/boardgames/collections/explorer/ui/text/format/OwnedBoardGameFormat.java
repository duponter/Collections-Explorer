package edu.boardgames.collections.explorer.ui.text.format;

import java.util.Set;
import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.Range;

public enum OwnedBoardGameFormat implements BiFunction<BoardGame, Set<String>, String> {
	BARE {
		@Override
		public String apply(BoardGame boardGame, Set<String> owners) {
			return boardGame.name();
		}
	},
	SLIM {
		@Override
		public String apply(BoardGame boardGame, Set<String> owners) {
			return String.join("\t",
					"%-70s".formatted(StringUtils.abbreviate(boardGame.name(), 70)),
					boardGame.year(),
					Score.score10().fullString(boardGame.bggScore()),
					String.join(", ", owners));
		}
	},
	FULL {
		@Override
		public String apply(BoardGame boardGame, Set<String> owners) {
			Range<String> communityPlayerCount = boardGame.bestWithPlayerCount()
					.or(boardGame::recommendedWithPlayerCount)
					.orElseGet(boardGame::playerCount);
			return String.join("\t",
					"%-70s".formatted(StringUtils.abbreviate(boardGame.name(), 70)),
					boardGame.year(),
					"%5s>%-4s".formatted(boardGame.playerCount().formatted(), communityPlayerCount.formatted()),
					"%7s min".formatted(boardGame.playtime().formatted()),
					Score.score10().fullString(boardGame.bggScore()),
					Score.score5().fullString(boardGame.averageWeight()),
					String.join(", ", owners));
		}
	};
}
