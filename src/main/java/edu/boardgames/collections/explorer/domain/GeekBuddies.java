package edu.boardgames.collections.explorer.domain;

import java.util.List;

public interface GeekBuddies {
	List<GeekBuddy> all();

	GeekBuddy one(String username);

	List<GeekBuddy> withUsername(String... usernames);
}
