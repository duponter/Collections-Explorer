package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.GeekBuddies;
import edu.boardgames.collections.explorer.domain.GeekBuddy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GeekBuddiesBggInMemory implements GeekBuddies {
	@Override
	public List<GeekBuddy> all() {
		return List.of(
				new GeekBuddyBgg("duponter", "Erwin"),
				new GeekBuddyBgg("jarrebesetoert", "Koen"),
				new GeekBuddyBgg("WouterAerts", "Wouter"),
				new GeekBuddyBgg("bartie", "Bart"),
				new GeekBuddyBgg("de rode baron", "Steffen"),
				new GeekBuddyBgg("Edou", "Edouard"),
				new GeekBuddyBgg("evildee", "Didier"),
				new GeekBuddyBgg("ForumMortsel", "Mortsel"),
				new GeekBuddyBgg("Svennos", "Sven"),
				new GeekBuddyBgg("TurtleR6", "Dirk")
		);
	}

	@Override
	public List<GeekBuddy> withUsername(String... usernames) {
		return Arrays.stream(usernames)
				.map(username -> new GeekBuddyBgg(username, username))
				.collect(Collectors.toList());
	}
}
