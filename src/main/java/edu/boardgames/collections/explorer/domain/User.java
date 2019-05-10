package edu.boardgames.collections.explorer.domain;

public class User {
	private final String firstName;
	private final String profileName;

	public User(String firstName, String profileName) {
		// validate with https://github.com/making/yavi
		this.firstName = firstName;
		this.profileName = profileName;
	}
}
