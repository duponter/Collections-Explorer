package edu.boardgames.collections.explorer;

import java.lang.System.Logger.Level;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.BoardGameAggregate;
import edu.boardgames.collections.explorer.domain.BoardGameAggregates;
import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.ui.input.BestWithInput;
import edu.boardgames.collections.explorer.ui.input.CollectionsInput;
import edu.boardgames.collections.explorer.ui.input.GeekBuddyInput;
import edu.boardgames.collections.explorer.ui.input.Input;
import edu.boardgames.collections.explorer.ui.input.OwnedBoardGameFormatInput;
import edu.boardgames.collections.explorer.ui.text.Chapter;
import edu.boardgames.collections.explorer.ui.text.ChapterTitle;
import edu.boardgames.collections.explorer.ui.text.Document;
import edu.boardgames.collections.explorer.ui.text.DocumentTitle;
import edu.boardgames.collections.explorer.ui.text.Line;
import edu.boardgames.collections.explorer.ui.text.LinesParagraph;
import edu.boardgames.collections.explorer.ui.text.format.OwnedBoardGameFormat;

@Path("/session")
public class SessionsResource {
    private static final System.Logger LOGGER = System.getLogger(SessionsResource.class.getName());

    @GET
    @Path("/play")
    @Produces(MediaType.TEXT_PLAIN)
    public String current(@QueryParam("collections") List<String> collections, @QueryParam("bestWith") Integer bestWith, @QueryParam("format") String format) {
        CollectionsInput searchableCollections = new CollectionsInput(collections);
        var bestWithInput = BestWithInput.of(bestWith);
        OwnedBoardGameFormatInput formatInput = new OwnedBoardGameFormatInput(format);
        String documentTitle = "Search collections %s to play a best with %s game, %s".formatted(searchableCollections.asText(), bestWithInput.asText(), formatInput.asText());
        LOGGER.log(Level.INFO, documentTitle);

        OwnedBoardGameFormat outputFormat = formatInput.resolve();
        return new Document(
                new DocumentTitle(documentTitle),
                new LinesParagraph(
                        groupAvailableBoardGames(searchableCollections, BoardGameGrouping.notGrouped(), bestWithInput)
                                .values().stream()
                                .flatMap(Set::stream)
                                .map(groupedBoardGame -> groupedBoardGame.asLine(outputFormat))
                                .toList()
                )
        ).toText();
    }

    @GET
    @Path("/play/{geekbuddy}")
    @Produces(MediaType.TEXT_PLAIN)
    public String current(@PathParam("geekbuddy") String geekbuddy, @QueryParam("collections") List<String> collections, @QueryParam("bestWith") Integer bestWith, @QueryParam("format") String format) {
        CollectionsInput searchableCollections = new CollectionsInput(collections);
        var bestWithInput = BestWithInput.of(bestWith);
        OwnedBoardGameFormatInput formatInput = new OwnedBoardGameFormatInput(format);
        GeekBuddyInput geekBuddyInput = new GeekBuddyInput(geekbuddy);
        String documentTitle = "Search collections %s to play a best with %s game, tailored for %s, %s".formatted(searchableCollections.asText(), bestWithInput.asText(), geekBuddyInput.asText(), formatInput.asText());
        LOGGER.log(Level.INFO, documentTitle);

        OwnedBoardGameFormat outputFormat = formatInput.resolve();
        return new Document(
            new DocumentTitle(documentTitle),
            groupAvailableBoardGames(searchableCollections, new GeekBuddyGrouping(geekBuddyInput.resolve()), bestWithInput)
                .entrySet().stream()
                .map(entry -> new Chapter(new ChapterTitle(entry.getKey().title()), new LinesParagraph(entry.getValue().stream().map(pc -> pc.asLine(outputFormat)).toList())))
                .toArray(Chapter[]::new)
        ).toText();
    }

    @GET
    @Path("/play2/{geekbuddy}")
    @Produces(MediaType.TEXT_PLAIN)
    public String current2(@PathParam("geekbuddy") String geekbuddy, @QueryParam("collections") List<String> collections, @QueryParam("bestWith") Integer bestWith, @QueryParam("format") String format) {
        CollectionsInput searchableCollections = new CollectionsInput(collections);
        var bestWithInput = BestWithInput.of(bestWith);
        OwnedBoardGameFormatInput formatInput = new OwnedBoardGameFormatInput(format);
        GeekBuddyInput geekBuddyInput = new GeekBuddyInput(geekbuddy);
        String documentTitle = "Search collections %s to play a best with %s game, tailored for %s, %s".formatted(searchableCollections.asText(), bestWithInput.asText(), geekBuddyInput.asText(), formatInput.asText());
        LOGGER.log(Level.INFO, documentTitle);

        OwnedBoardGameFormat outputFormat = formatInput.resolve();
        GeekBuddy buddy = geekBuddyInput.resolve();
        List<Line> map = new BoardGameAggregate(searchableCollections.resolve())
            .flatten(BoardGameAggregates::joinCollectionNames)
            .filter(BoardGameAggregates.boardGame(bestWithInput.resolve()))
            .merge(buddy.playedCollection(), BoardGameAggregates::addsPlayedInfo)
            .merge(buddy.wantToPlayCollection(), BoardGameAggregates::toggleWantToPlay)
            .map((mbg, bg) -> Line.of((mbg.wantToPlay() ? "W" : " ") + (mbg.played() ? "P" : " ") + Objects.toString(mbg.rating(), "-") + outputFormat.apply(bg, Set.of()) + mbg.collection()))
            .toSortedListBy(Line::line);

        return new Document(
            new DocumentTitle(documentTitle),
            new Chapter(new ChapterTitle("Chapitre"), new LinesParagraph(map))
        ).toText();
    }

    private Map<PlayGroup, Set<GroupedBoardGame>> groupAvailableBoardGames(CollectionsInput searchableCollections, BoardGameGrouping grouping, Input<Predicate<BoardGame>> bestWithInput) {
        return searchableCollections.resolve().copiesPerBoardGame()
            .entrySet().stream()
            .filter(entry -> bestWithInput.resolve().test(entry.getKey()))
            .map(entry -> new GroupedBoardGame(grouping.group(entry.getKey()), entry.getKey(), entry.getValue()))
            .collect(Collectors.groupingBy(GroupedBoardGame::group, TreeMap::new, Collectors.toCollection(TreeSet::new)));
    }

    private record GroupedBoardGame(PlayGroup group, BoardGame boardGame, Set<String> owners) implements Comparable<GroupedBoardGame> {
        private static final Comparator<GroupedBoardGame> COMPARATOR = Comparator.comparing(GroupedBoardGame::group).thenComparing(pc -> pc.boardGame().name())
                .thenComparing(pc -> pc.boardGame().year());

        public Line asLine(OwnedBoardGameFormat format) {
            return Line.of(format.apply(this.boardGame(), this.owners()));
        }

        @Override
        public int compareTo(GroupedBoardGame pc) {
            return COMPARATOR.compare(this, pc);
        }
    }

    private interface BoardGameGrouping {
        static BoardGameGrouping notGrouped() {
            return bg -> PlayGroup.NOT_GROUPED;
        }

        PlayGroup group(BoardGame boardGame);
    }

    private record GeekBuddyGrouping(BoardGameCollection wantToPlay, BoardGameCollection played) implements BoardGameGrouping {
        private GeekBuddyGrouping(GeekBuddy buddy) {
            this(buddy.wantToPlayCollection(), buddy.playedCollection());
        }

        @Override
        public PlayGroup group(BoardGame boardGame) {
            if (wantToPlay.contains(boardGame)) {
                return played.contains(boardGame) ? PlayGroup.WANT_ALREADY_PLAYED : PlayGroup.WANT_NEVER_PLAYED;
            }
            return played.contains(boardGame) ? PlayGroup.ALREADY_PLAYED : PlayGroup.NEVER_PLAYED;
        }
    }

    private enum PlayGroup {
        WANT_NEVER_PLAYED("Want to play (never played)"),
        WANT_ALREADY_PLAYED("Want to play again (long time ago or to give another chance)"),
        WANT_TOP_RATED("Want to play again (rated 8 or higher)"),
        ALREADY_PLAYED("Already played"),
        NEVER_PLAYED("Never played"),
        NOT_GROUPED("");

        private final String title;

        PlayGroup(String title) {
            this.title = title;
        }

        public String title() {
            return title;
        }
    }
}
