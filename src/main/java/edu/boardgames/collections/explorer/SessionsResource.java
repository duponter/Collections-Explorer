package edu.boardgames.collections.explorer;

import java.lang.System.Logger.Level;
import java.time.Year;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.BoardGameAggregate;
import edu.boardgames.collections.explorer.domain.BoardGameAggregates;
import edu.boardgames.collections.explorer.domain.CollectedBoardGame;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.ui.input.BestWithInput;
import edu.boardgames.collections.explorer.ui.input.CollectionsInput;
import edu.boardgames.collections.explorer.ui.input.GeekBuddyInput;
import edu.boardgames.collections.explorer.ui.input.OwnedBoardGameFormatInput;
import edu.boardgames.collections.explorer.ui.text.Chapter;
import edu.boardgames.collections.explorer.ui.text.ChapterTitle;
import edu.boardgames.collections.explorer.ui.text.Document;
import edu.boardgames.collections.explorer.ui.text.DocumentTitle;
import edu.boardgames.collections.explorer.ui.text.Line;
import edu.boardgames.collections.explorer.ui.text.LinesParagraph;
import edu.boardgames.collections.explorer.ui.text.format.OwnedBoardGameFormat;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.tuple.Pair;

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
                new BoardGameAggregate(searchableCollections.resolve())
                    .flatten(BoardGameAggregates::joinCollectionNames)
                    .filter(BoardGameAggregates.boardGame(bestWithInput.resolve()))
                    .map((collectedBoardGame, boardGame) -> Line.of(outputFormat.apply(boardGame, Set.of(collectedBoardGame.collection()))))
                    .toSortedList()
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
        GeekBuddy buddy = geekBuddyInput.resolve();
        Multimap<PlayGroup, GroupableBoardGame> grouped = new BoardGameAggregate(searchableCollections.resolve())
            .flatten(BoardGameAggregates::joinCollectionNames)
            .filter(BoardGameAggregates.boardGame(bestWithInput.resolve()))
            .merge(buddy.playedCollection(), BoardGameAggregates::addsPlayedInfo)
            .merge(buddy.wantToPlayCollection(), BoardGameAggregates::toggleWantToPlay)
            .map(GroupableBoardGame::new)
            .groupBy(GroupableBoardGame::group);

        return new Document(
            new DocumentTitle(documentTitle),
            grouped.keyMultiValuePairsView().toSortedList(Comparator.comparing(Pair::getOne)).collect(p -> toChapter(p.getOne(), p.getTwo(), outputFormat)).toArray(new Chapter[0])

        ).toText();
    }

    private Chapter toChapter(PlayGroup group, RichIterable<GroupableBoardGame> boardGames, OwnedBoardGameFormat outputFormat) {
        Comparator<GroupableBoardGame> comparator = Comparator.comparing((GroupableBoardGame gbg) -> -Objects.requireNonNullElse(gbg.collectedBoardGame().rating(), 0))
            .thenComparing(gbg -> gbg.boardGame().name());
        return new Chapter(
            new ChapterTitle(group.title()),
            new LinesParagraph(
                boardGames.toImmutableSortedList(comparator)
                    .collect(gbg -> Line.of((gbg.collectedBoardGame().played() ? Objects.toString(gbg.collectedBoardGame().rating(), " ") : " ") + " " + outputFormat.apply(gbg.boardGame(), Set.of()) + gbg.collectedBoardGame().collection()))
                    .toArray(new Line[0])
            )
        );
    }

    private record GroupableBoardGame(CollectedBoardGame collectedBoardGame, BoardGame boardGame) {
        public PlayGroup group() {
            if (collectedBoardGame().wantToPlay()) {
                return collectedBoardGame.played() ? PlayGroup.WANT_ALREADY_PLAYED : PlayGroup.WANT_NEVER_PLAYED;
            }
            if (collectedBoardGame.played()) {
                return PlayGroup.ALREADY_PLAYED;
            } else if (Integer.parseInt(boardGame.year()) >= Year.now().getValue() - 2) {
                return PlayGroup.NOT_YET_PLAYED_NEW;
            }
            return PlayGroup.NEVER_PLAYED;
        }
    }

private enum PlayGroup {
    WANT_NEVER_PLAYED("Want to play (never played)"),
    WANT_ALREADY_PLAYED("Want to play again (long time ago or to give another chance)"),
    WANT_TOP_RATED("Want to play again (rated 8 or higher)"),
    ALREADY_PLAYED("Already played"),
    NOT_YET_PLAYED_NEW("Not yet played this new game"),
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
