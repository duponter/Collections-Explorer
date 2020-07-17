package edu.boardgames.collections.explorer.domain;

import java.util.List;

public interface GeekLists {
	default List<GeekList> all() {
		return List.of(withId("274761"));   // Borrowed Board Games
	}

	GeekList withId(String id);
}
