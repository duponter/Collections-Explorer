package edu.boardgames.collections.explorer.ui.text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

public record Column<T>(String header, int width, Function<T, String> valueProcessor) {
    public String renderHeader(String prefix, String suffix) {
		return this.render(prefix, suffix, this.header);
	}

	public String renderLine(String prefix, String suffix) {
        return this.render(prefix, suffix, "-".repeat(this.width)).replace(' ', '-');
    }

    public String renderValue(String prefix, String suffix, T value) {
        return this.render(prefix, suffix, valueProcessor.apply(value));
    }

    private String render(String prefix, String suffix, String value) {
        return String.join("", prefix, StringUtils.center(value, width, ' '), suffix);
    }

    public record Formatted(String format) implements Function<String, String> {
        @Override
        public String apply(String value) {
            return format().formatted(value);
        }
    }

    public record Date() implements Function<LocalDate, String> {
        private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

        @Override
        public String apply(LocalDate date) {
            return date.format(FORMAT);
        }
    }
}
