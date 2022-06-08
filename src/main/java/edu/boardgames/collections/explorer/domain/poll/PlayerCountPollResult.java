package edu.boardgames.collections.explorer.domain.poll;

import org.apache.commons.lang3.StringUtils;

public interface PlayerCountPollResult extends PollResult, Comparable<PlayerCountPollResult> {
    String numberOfPlayers();

    @Override
    default int compareTo(PlayerCountPollResult other) {
        if (StringUtils.equals(this.value(), other.value())) {
            return StringUtils.compare(this.value(), other.value());
        }
        return StringUtils.compare(this.numberOfPlayers(), other.numberOfPlayers());
    }
}
