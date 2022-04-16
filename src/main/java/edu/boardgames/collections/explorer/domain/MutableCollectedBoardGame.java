package edu.boardgames.collections.explorer.domain;

public class MutableCollectedBoardGame implements CollectedBoardGame {
    private final String id;
    private String name;
    private final String year;
    private String collection;
    private final String originalName;
    private Integer rating;
    private boolean owned;
    private boolean previouslyOwned;
    private boolean forTrade;
    private boolean wanted;
    private boolean wantToPlay;
    private boolean wantToBuy;
    private boolean wishlisted;
    private boolean preordered;
    private Integer numberOfPlays;
    private String publicComment;

    public MutableCollectedBoardGame(BoardGameSummary summary) {
        this.id = summary.id();
        this.name = summary.name();
        this.year = summary.year();
        this.originalName = summary.name();
    }

    public MutableCollectedBoardGame(CollectedBoardGame source) {
        this.id = source.id();
        this.name = source.name();
        this.year = source.year();
        this.collection = source.collection();
        this.originalName = source.originalName();
        this.rating = source.rating();
        this.owned = source.owned();
        this.previouslyOwned = source.previouslyOwned();
        this.forTrade = source.forTrade();
        this.wanted = source.wanted();
        this.wantToPlay = source.wantToPlay();
        this.wantToBuy = source.wantToBuy();
        this.wishlisted = source.wishlisted();
        this.preordered = source.preordered();
        this.numberOfPlays = source.numberOfPlays();
        this.publicComment = source.publicComment();
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    public MutableCollectedBoardGame name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String year() {
        return year;
    }

    @Override
    public String collection() {
        return collection;
    }

    public MutableCollectedBoardGame collection(String collection) {
        this.collection = collection;
        return this;
    }

    @Override
    public String originalName() {
        return originalName;
    }

    @Override
    public Integer rating() {
        return rating;
    }

    public MutableCollectedBoardGame rating(Integer rating) {
        this.rating = rating;
        return this;
    }

    @Override
    public boolean owned() {
        return owned;
    }

    public MutableCollectedBoardGame owned(boolean owned) {
        this.owned = owned;
        return this;
    }

    @Override
    public boolean previouslyOwned() {
        return previouslyOwned;
    }

    public MutableCollectedBoardGame previouslyOwned(boolean previouslyOwned) {
        this.previouslyOwned = previouslyOwned;
        return this;
    }

    @Override
    public boolean forTrade() {
        return forTrade;
    }

    public MutableCollectedBoardGame forTrade(boolean forTrade) {
        this.forTrade = forTrade;
        return this;
    }

    @Override
    public boolean wanted() {
        return wanted;
    }

    public MutableCollectedBoardGame wanted(boolean wanted) {
        this.wanted = wanted;
        return this;
    }

    @Override
    public boolean wantToPlay() {
        return wantToPlay;
    }

    public MutableCollectedBoardGame wantToPlay(boolean wantToPlay) {
        this.wantToPlay = wantToPlay;
        return this;
    }

    @Override
    public boolean wantToBuy() {
        return wantToBuy;
    }

    public MutableCollectedBoardGame wantToBuy(boolean wantToBuy) {
        this.wantToBuy = wantToBuy;
        return this;
    }

    @Override
    public boolean wishlisted() {
        return wishlisted;
    }

    public MutableCollectedBoardGame wishlisted(boolean wishlisted) {
        this.wishlisted = wishlisted;
        return this;
    }

    @Override
    public boolean preordered() {
        return preordered;
    }

    public MutableCollectedBoardGame preordered(boolean preordered) {
        this.preordered = preordered;
        return this;
    }

    @Override
    public Integer numberOfPlays() {
        return numberOfPlays;
    }

    public MutableCollectedBoardGame numberOfPlays(Integer numberOfPlays) {
        this.numberOfPlays = numberOfPlays;
        return this;
    }

    @Override
    public String publicComment() {
        return publicComment;
    }

    public MutableCollectedBoardGame publicComment(String publicComment) {
        this.publicComment = publicComment;
        return this;
    }
}
