package edu.boardgames.collections.explorer;

import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.CollectedBoardGame;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.domain.GeekList;
import edu.boardgames.collections.explorer.domain.MutableCollectedBoardGame;
import edu.boardgames.collections.explorer.domain.poll.PlayerCountPollResult;
import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;
import edu.boardgames.collections.explorer.ui.input.BestWithInput;
import edu.boardgames.collections.explorer.ui.input.BoardGameIdInput;
import edu.boardgames.collections.explorer.ui.input.GeekBuddyInput;
import edu.boardgames.collections.explorer.ui.input.Input;
import edu.boardgames.collections.explorer.ui.text.Chapter;
import edu.boardgames.collections.explorer.ui.text.ChapterTitle;
import edu.boardgames.collections.explorer.ui.text.Document;
import edu.boardgames.collections.explorer.ui.text.DocumentTitle;
import edu.boardgames.collections.explorer.ui.text.Line;
import edu.boardgames.collections.explorer.ui.text.LinesParagraph;
import edu.boardgames.collections.explorer.ui.text.TabbedRecordList;
import edu.boardgames.collections.explorer.ui.text.format.OwnedBoardGameFormat;
import edu.boardgames.collections.explorer.ui.text.format.PerspectivedBoardGame;

@Path("/collections")
public class CollectionsResource {
    private static final System.Logger LOGGER = System.getLogger(CollectionsResource.class.getName());

    @GET
    @Path("/wanttoplay/{geekbuddy}")
    @Produces(MediaType.TEXT_PLAIN)
    public String wantToPlay(@PathParam("geekbuddy") String geekbuddy, @QueryParam("bestWith") Integer bestWith, @QueryParam("includeRated") Integer minimallyRated) {
        LOGGER.log(Level.INFO, "Search collections of all geekbuddies for {0}'s want-to-play best with {1,number,integer} games", geekbuddy, bestWith);

        GeekBuddy buddy = BggInit.get().geekBuddies().one(geekbuddy);
        List<BoardGame> wantToPlay = buddy.wantToPlayCollection().boardGamesDetailed();
        LOGGER.log(Level.INFO, "Collection fetched: {0} wants to play {1,number,integer} boardgames.", geekbuddy, wantToPlay.size());

        Stream<BoardGame> all = wantToPlay.stream();
        if (minimallyRated != null) {
            List<BoardGame> rated = buddy.ratedCollection(minimallyRated).boardGamesDetailed();
            LOGGER.log(Level.INFO, "Collection fetched: {0} rated {1,number,integer} boardgames {1,number,integer} or more.", geekbuddy, rated.size(), minimallyRated);
            all = Stream.concat(all, rated.stream());
        }

        Input<Predicate<BoardGame>> bestWithInput = BestWithInput.of(bestWith);
        return matchAgainstAllCollections(all.filter(bestWithInput.resolve()), "Search collections of all geekbuddies for %s's want-to-play %s games".formatted(geekbuddy, bestWithInput.asText()));
    }

    @GET
    @Path("/boardgame/{ids}")
    @Produces(MediaType.TEXT_PLAIN)
    public String lookup(@PathParam("ids") String ids) {
        BoardGameIdInput boardGameIdInput = new BoardGameIdInput(ids);
        List<BoardGame> boardGames = BggInit.get().boardGames().withIds(boardGameIdInput.resolve().stream());
        if (boardGames.isEmpty()) {
            return "BoardGame(s) with id [%s] not found or does not exists on BGG".formatted(boardGameIdInput.asText());
        }
        return matchAgainstAllCollections(boardGames.stream(), "Search collections of all geekbuddies for " + boardGameIdInput.asText());
    }

    private String matchAgainstAllCollections(Stream<BoardGame> all, String title) {
        Map<BoardGame, Set<String>> available = BggInit.get().collections().all().copiesPerBoardGame();
        List<Line> copies = all
            .map(bg -> OwnedBoardGameFormat.FULL.apply(bg, available.getOrDefault(bg, Set.of())))
            .sorted()
            .map(Line::of)
            .toList();
        LOGGER.log(Level.INFO, "All owned collections matched against list of board games");

        return new Document(
            new DocumentTitle(title),
            new LinesParagraph(copies)
        ).toText();
    }

    @GET
    @Path("/boardgame/{id}/decorated")
    @Produces(MediaType.TEXT_PLAIN)
    public String decorate(@PathParam("id") String id, @QueryParam("geekbuddy") String geekbuddy) {
        BoardGameIdInput boardGameIdInput = new BoardGameIdInput(id);
        GeekBuddyInput geekBuddyInput = new GeekBuddyInput(geekbuddy);
        String documentTitle = "Display details of %s decorated for %s with geeklist entries".formatted(boardGameIdInput.asText(), geekBuddyInput.asText());
        LOGGER.log(Level.INFO, documentTitle);

        List<BoardGame> boardGames = BggInit.get().boardGames().withIds(boardGameIdInput.resolve().stream());
        if (boardGames.isEmpty()) {
            return "BoardGame with id [%s] not found or does not exists on BGG".formatted(boardGameIdInput.asText());
        }

        List<GeekList> geekLists = BggInit.get().geekLists().withId("278886", "279856");

        return new Document(
            new DocumentTitle(documentTitle),
            Stream.concat(
                Stream.of(
                    new Chapter(
                        new ChapterTitle("General Information"),
                        new TabbedRecordList<>(boardGames.stream().map(BoardGameWrapper::new).toList(), OwnedBoardGameFormat.BARE.toColumnLayout())
                    ),
                    new Chapter(
                        new ChapterTitle("Player Count"),
                        new LinesParagraph(
                            boardGames.stream()
                                .flatMap(bg -> bg.playerCountPoll().results().collect(PlayerCountPollResultLine::new).toSortedList().stream())
                                .toList()
                        )
                    ),
                    new Chapter(
                        new ChapterTitle("Personal Information"),
                        new LinesParagraph(Line.of(geekBuddyInput.resolve().ownedCollection().name()))
                    )
                ),
                geekLists.stream()
                    .map(geekList -> toChapter(geekList.asCollection()))
            ).toList()
        ).toText();
    }

    private record PlayerCountPollResultLine(PlayerCountPollResult result) implements Line {
        @Override
        public String line() {
            return "%s %s: %d".formatted(result.value(), result.numberOfPlayers(), result.votes());
        }
    }

    private record BoardGameWrapper(BoardGame boardGame, CollectedBoardGame collectedBoardGame) implements PerspectivedBoardGame {
        public BoardGameWrapper(BoardGame boardGame) {
            this(boardGame, new MutableCollectedBoardGame(boardGame).collection(""));
        }
    }

    private Chapter toChapter(BoardGameCollection collection) {
        return new Chapter(
            new ChapterTitle(collection.name()),
            new LinesParagraph(
                collection.boardGames()
                    .collect(CollectedBoardGame::publicComment)
                    .collect(c -> StringUtils.defaultIfEmpty(c, "n/a"))
                    .collect(Line::of)
                    .castToList()
            )
        );
    }
}
