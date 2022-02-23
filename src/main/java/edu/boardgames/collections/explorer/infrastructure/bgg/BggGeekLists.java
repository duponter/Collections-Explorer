package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.GeekList;
import edu.boardgames.collections.explorer.domain.GeekLists;

public class BggGeekLists implements GeekLists {
	@Override
	public GeekList withId(String id) {
        return new GeekListEndpoint(id).execute();
	}
}
