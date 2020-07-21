package edu.boardgames.collections.explorer.domain;

import java.time.LocalDate;

public interface Play {
	String id();

	LocalDate date();

	int quantity();

	boolean incomplete();

	String location();

	String boardGameId();
}
