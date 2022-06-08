package edu.boardgames.collections.explorer.domain.poll;

import org.eclipse.collections.api.list.ImmutableList;

public interface Poll<R extends PollResult> {
    String name();

    String title();

    int totalVotes();

    ImmutableList<R> results();
}
