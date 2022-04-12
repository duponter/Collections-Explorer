package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.List;
import java.util.Map;

import edu.boardgames.collections.explorer.domain.GeekBuddies;
import edu.boardgames.collections.explorer.domain.GeekBuddy;

import static java.util.Map.entry;

/*
GeekBuddy groups:
borrowed - geeklist - 274761 - Borrowed Board Games

fmlimited
bareelstraat
sirplayalot
mine: duponter + borrowed
 */
public class GeekBuddiesBggInMemory implements GeekBuddies {
    private static final Map<String, GeekBuddy> BUDDIES = Map.ofEntries(
            entry("duponter", new GeekBuddyBgg("duponter", "Erwin Dupont")),
            entry("jarrebesetoert", new GeekBuddyBgg("jarrebesetoert", "Koen Lostrie")),
            entry("WouterAerts", new GeekBuddyBgg("WouterAerts", "Wouter Aerts")),
            entry("bartie", new GeekBuddyBgg("bartie", "Bart De Vré")),
            entry("de rode baron", new GeekBuddyBgg("de rode baron", "Steffen Wendelen")),
            entry("Edou", new GeekBuddyBgg("Edou", "Edouard Van Belle")),
            entry("evildee", new GeekBuddyBgg("evildee", "Didier De Breuck")),
            entry("ForumMortsel", new GeekBuddyBgg("ForumMortsel", "FORUM Mortsel")),
            entry("FFED", new GeekBuddyBgg("FFED", "FORUM Federatie")),
            entry("Svennos", new GeekBuddyBgg("Svennos", "Sven Talboom")),
            entry("TurtleR6", new GeekBuddyBgg("TurtleR6", "Dirk Frederickx")),
            entry("engelwi", new GeekBuddyBgg("engelwi", "Wim Engels")),
            entry("wallofshame", new GeekBuddyBgg("wallofshame", "Johan Drubbel")),
            entry("leys", new GeekBuddyBgg("leys", "Pieter Leys")),
            entry("dierenh", new GeekBuddyBgg("dierenh", "Hilde Vandierendonck"))

    );

	@Override
	public List<GeekBuddy> all() {
        return List.copyOf(BUDDIES.values());
	}

	@Override
	public GeekBuddy one(String username) {
        return BUDDIES.getOrDefault(username, new GeekBuddyBgg(username, username));
	}
}
