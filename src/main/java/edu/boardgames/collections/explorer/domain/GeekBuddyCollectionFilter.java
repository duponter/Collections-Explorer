package edu.boardgames.collections.explorer.domain;

public enum GeekBuddyCollectionFilter {
    NONE {
        @Override
        public String id(GeekBuddy geekBuddy) {
            return geekBuddy.username();
        }

        @Override
        public String name(GeekBuddy geekBuddy) {
            return geekBuddy.name();
        }
    },
    OWNED,
    PREORDERED,
    WANT_TO_PLAY,
    RATED,
    PLAYED;

    public String id(GeekBuddy geekBuddy) {
        return String.join(".", geekBuddy.username(), this.name().toLowerCase());
    }

    public String name(GeekBuddy geekBuddy) {
        return "%s [%s]".formatted(geekBuddy.name(), this.name().toLowerCase().replace("_", "-"));
    }
}
