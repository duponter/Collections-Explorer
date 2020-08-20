package edu.boardgames.collections.explorer;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.Range;

public class BoardGameJson {
	public String id;
	public String name;
	public String year;
	public Double bggScore;
	public String playerCount;
	public String bestWithPlayerCount;
	public String recommendedWithPlayerCount;
	public String playtime;
	public Double averageWeight;

	public BoardGameJson() {
	}

	public BoardGameJson(BoardGame input) {
		this.id = input.id();
		this.name = input.name();
		this.year = input.year();
		this.bggScore = input.bggScore();
		this.playerCount = input.playerCount().formatted();
		this.bestWithPlayerCount = input.bestWithPlayerCount().map(Range::formatted).orElse("none");
		this.recommendedWithPlayerCount = input.recommendedWithPlayerCount().map(Range::formatted).orElse("none");
		this.playtime = input.playtime().formatted();
		this.averageWeight = input.averageWeight();
	}
}
