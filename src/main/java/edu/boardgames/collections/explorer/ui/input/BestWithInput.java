package edu.boardgames.collections.explorer.ui.input;

import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.PlayerCount;

public class BestWithInput implements Input<Predicate<BoardGame>> {
	private static final Input<Predicate<BoardGame>> DUMMY = new Input<>() {
		@Override
		public String asText() {
			return StringUtils.EMPTY;
		}

		@Override
		public Predicate<BoardGame> resolve() {
			return bg -> true;
		}
	};

	private final int bestWith;

	public static Input<Predicate<BoardGame>> of(Integer bestWith) {
        // allow multiple best with input
		return bestWith != null ? new BestWithInput(bestWith) : DUMMY;
	}

	private BestWithInput(int bestWith) {
		this.bestWith = bestWith;
	}

	@Override
	public String asText() {
		return "best with " + bestWith;
	}

	@Override
	public Predicate<BoardGame> resolve() {
		return new PlayerCount(bestWith)::bestOnly;
	}
}
