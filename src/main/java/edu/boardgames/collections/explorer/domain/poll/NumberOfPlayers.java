package edu.boardgames.collections.explorer.domain.poll;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public record NumberOfPlayers(int count, String suffix) implements Comparable<NumberOfPlayers> {
    private static final Pattern PATTERN = Pattern.compile("^(\\d+)([+])?$");

    public static NumberOfPlayers of(String input) {
        Matcher matcher = PATTERN.matcher(input);
        if (matcher.matches()) {
            return new NumberOfPlayers(Integer.parseInt(matcher.group(1)), StringUtils.defaultString(matcher.group(2)));
        } else {
            throw new IllegalArgumentException(String.format("Input [%s] is not supported as number of players", input));
        }
    }

    public String value() {
        return this.count + this.suffix;
    }

    @Override
    public int compareTo(NumberOfPlayers other) {
        if (other == null) {
            return 1;
        }
        if (this.count == other.count) {
            return this.suffix.compareTo(other.suffix);
        } else {
            return this.count - other.count;
        }
    }
}
