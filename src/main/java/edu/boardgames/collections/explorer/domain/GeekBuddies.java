package edu.boardgames.collections.explorer.domain;

import java.util.List;

public interface GeekBuddies {
	List<GeekBuddy> all();

	List<GeekBuddy> withUsername(String... usernames);
}
