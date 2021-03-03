package edu.boardgames.collections.explorer.domain;

import java.time.LocalDate;
import java.util.List;

public interface Play {
	String id();

	LocalDate date();

	int quantity();

	int length();

	boolean incomplete();

	String location();

	String boardGameId();

	String comments();

	List<Player> players();
}
