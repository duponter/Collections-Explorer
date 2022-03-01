package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import edu.boardgames.collections.explorer.domain.GeekBuddies;
import edu.boardgames.collections.explorer.domain.GeekBuddy;

/*
GeekBuddy groups:
borrowed - geeklist - 274761 - Borrowed Board Games

fmlimited
bareelstraat
sirplayalot
mine: duponter + borrowed
 */
public class GeekBuddiesBggInMemory implements GeekBuddies {
	@Override
	public List<GeekBuddy> all() {
		return List.of(
				new GeekBuddyBgg("duponter", "Erwin Dupont"),
				new GeekBuddyBgg("jarrebesetoert", "Koen Lostrie"),
				new GeekBuddyBgg("WouterAerts", "Wouter Aerts"),
				new GeekBuddyBgg("bartie", "Bart De Vré"),
				new GeekBuddyBgg("de rode baron", "Steffen Wendelen"),
				new GeekBuddyBgg("Edou", "Edouard Van Belle"),
				new GeekBuddyBgg("evildee", "Didier De Breuck"),
				new GeekBuddyBgg("ForumMortsel", "FORUM Mortsel"),
//				new GeekBuddyBgg("FFED", "FORUM Federatie"),
				new GeekBuddyBgg("Svennos", "Sven Talboom"),
				new GeekBuddyBgg("TurtleR6", "Dirk Frederickx"),
				new GeekBuddyBgg("engelwi", "Wim Engels"),
				new GeekBuddyBgg("wallofshame", "Johan Drubbel"),
				new GeekBuddyBgg("leys", "Pieter Leys")
//				new GeekBuddyBgg("dierenh", "Hilde Vandierendonck")
		);
	}

	@Override
	public GeekBuddy one(String username) {
		return new GeekBuddyBgg(username, username);
	}

	@Override
	public List<GeekBuddy> withUsername(String... usernames) {
		return Arrays.stream(usernames)
				.map(this::one)
				.collect(Collectors.toList());
	}
}
