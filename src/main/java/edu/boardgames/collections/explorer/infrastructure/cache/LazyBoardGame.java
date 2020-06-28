package edu.boardgames.collections.explorer.infrastructure.cache;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.Range;
import io.vavr.Lazy;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Objects;
import java.util.Optional;

public class LazyBoardGame implements BoardGame {
	private static final Logger LOGGER = System.getLogger(LazyBoardGame.class.getName());

	private final Lazy<String> id;
	private final Lazy<String> name;
	private final Lazy<String> year;
	private final Lazy<Double> bggScore;
	private final Lazy<Range<String>> playerCount;
	private final Lazy<Optional<Range<String>>> bestWithPlayerCount;
	private final Lazy<Optional<Range<String>>> recommendedWithPlayerCount;
	private final Lazy<Range<String>> playtime;
	private final Lazy<Double> averageWeight;

	protected LazyBoardGame(BoardGame delegate) {
		this.id = Lazy.of(delegate::id);
		this.name = Lazy.of(delegate::name);
		this.year = Lazy.of(delegate::year);
		this.bggScore = Lazy.of(delegate::bggScore);
		this.playerCount = Lazy.of(delegate::playerCount);
		this.bestWithPlayerCount = Lazy.of(delegate::bestWithPlayerCount);
		this.recommendedWithPlayerCount = Lazy.of(delegate::recommendedWithPlayerCount);
		this.playtime = Lazy.of(delegate::playtime);
		this.averageWeight = Lazy.of(delegate::averageWeight);
	}

	@Override
	public String id() {
		return this.id.get();
	}

	@Override
	public String name() {
		return this.name.get();
	}

	@Override
	public String year() {
		return this.year.get();
	}

	@Override
	public Double bggScore() {
		return this.bggScore.get();
	}

	@Override
	public Range<String> playerCount() {
		LOGGER.log(Level.DEBUG, String.format("[%s] Getting playerCount %s", this.name(), this.playerCount.get()));
		return this.playerCount.get();
	}

	@Override
	public Optional<Range<String>> bestWithPlayerCount() {
		LOGGER.log(Level.DEBUG, String.format("[%s] Getting bestWithPlayerCount %s", this.name(), this.bestWithPlayerCount.get()));
		return this.bestWithPlayerCount.get();
	}

	@Override
	public Optional<Range<String>> recommendedWithPlayerCount() {
		LOGGER.log(Level.DEBUG, String.format("[%s] Getting recommendedWithPlayerCount %s", this.name(), this.recommendedWithPlayerCount.get()));
		return this.recommendedWithPlayerCount.get();
	}

	@Override
	public Range<String> playtime() {
		return this.playtime.get();
	}

	@Override
	public Double averageWeight() {
		return this.averageWeight.get();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LazyBoardGame that = (LazyBoardGame) o;
		return id().equals(that.id());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id());
	}
}
