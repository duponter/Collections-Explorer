package edu.boardgames.collections.explorer.domain;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class MageKnightSoloPlayComments {
	private final Map<String, String> parsedComments;

	public MageKnightSoloPlayComments(String input) {
		this.parsedComments = parse(input);
	}

	private Map<String, String> parse(String input) {
		return input.lines()
				.map(line -> line.split(": "))
				.collect(Collectors.toMap(
						tokens -> StringUtils.upperCase(StringUtils.replaceChars(tokens[0], " ", "_")),
						tokens -> tokens[1])
				);
	}

	public String scenario() {
		return this.parsedComments.get("SCENARIO");
	}

	public String mageKnight() {
		return this.parsedComments.get("MAGE_KNIGHT");
	}

	public String dummyPlayer() {
		return this.parsedComments.getOrDefault("DUMMY_PLAYER", "");
	}
}
