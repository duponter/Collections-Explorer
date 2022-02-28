package edu.boardgames.collections.explorer.ui.input;

import java.util.List;
import java.util.regex.Pattern;

public record BoardGameIdInput(String ids) implements Input<List<String>> {
    private static final Pattern COMMA_SEPARATED = Pattern.compile("\\s*,\\s*");

    @Override
    public String asText() {
        return "boardgame(s) with id " + ids;
    }

    @Override
    public List<String> resolve() {
        return COMMA_SEPARATED.splitAsStream(ids).toList();
    }
}
