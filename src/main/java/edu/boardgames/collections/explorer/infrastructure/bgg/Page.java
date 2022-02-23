package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.lang.System.Logger;

import org.apache.commons.lang3.Validate;

import static java.lang.System.Logger.Level.DEBUG;

public record Page(int size) {
    private static final Logger LOGGER = System.getLogger(Page.class.getName());

    public Page {
        Validate.isTrue(size > 0, "Page size must be greater than 0");
    }

    public int count(int recordCount) {
        int count = (recordCount / size) + (recordCount % size > 0 ? 1 : 0);
        LOGGER.log(DEBUG, "Given max %d records per page, %d records result into %d page(s)".formatted(this.size(), recordCount, count));
        return count;
    }

    public int averageSize(int recordCount) {
        return recordCount == 0  ? 0 : recordCount / count(recordCount);
    }
}
