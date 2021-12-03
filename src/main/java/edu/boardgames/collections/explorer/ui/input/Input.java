package edu.boardgames.collections.explorer.ui.input;

public interface Input<T> {
	String asText();

	T resolve();
}
