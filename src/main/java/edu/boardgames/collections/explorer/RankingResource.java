package edu.boardgames.collections.explorer;

import java.lang.System.Logger;
import java.util.Comparator;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.BoardGameAggregate;
import edu.boardgames.collections.explorer.domain.BoardGameAggregates;
import edu.boardgames.collections.explorer.domain.poll.VotesPercentage;
import edu.boardgames.collections.explorer.ui.input.CollectionsInput;
import edu.boardgames.collections.explorer.ui.text.Column;
import edu.boardgames.collections.explorer.ui.text.Document;
import edu.boardgames.collections.explorer.ui.text.DocumentTitle;
import edu.boardgames.collections.explorer.ui.text.Table;
import edu.boardgames.collections.explorer.ui.text.format.Score;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.list.ImmutableList;

import static java.lang.System.getLogger;

@Path("/ranking")
public class RankingResource {
    private static final Logger LOGGER = getLogger(RankingResource.class.getName());

    @GET
    @Path("/playercount/{count}")
    @Produces(MediaType.TEXT_PLAIN)
    public String current(@PathParam("count") Integer playerCount, @QueryParam("collections") String collections) {
        CollectionsInput collectionsInput = new CollectionsInput(collections);
        LOGGER.log(Logger.Level.INFO, "Search currently playable collections {0} to rank games for {1} players", collectionsInput.asText(), playerCount);
        return new Document(
            new DocumentTitle("Search currently playable collections %s to rank games for %s players".formatted(collections, playerCount)),
            new Table<>(
                new BoardGameAggregate(collectionsInput.resolve())
                    .flatten(BoardGameAggregates::joinCollectionNames)
                    .map((cbg, entry) -> new Ranking(entry, cbg.collection(), playerCount))
                    .toSortedList(Comparator.comparing(r1 -> r1.boardGame().name())),
                List.of(
                    new Column<>("Boardgame", 70, r -> "%-70s".formatted(StringUtils.abbreviate(r.boardGame().name(), 70))),
                    new Column<>("Year", 4, r -> r.boardGame().year()),
                    new Column<>("Players", 7, r -> String.format("%5sp", r.boardGame().playerCount().formatted())),
                    new Column<>("Playtime", 11, r -> String.format("%7s min", r.boardGame().playtime().formatted())),
                    new Column<>("Weight", 6, r -> Score.score5().apply(r.boardGame().averageWeight())),
                    new Column<>("BGG Score", 10, r -> Score.score10().apply(r.boardGame().bggScore())),
                    new Column<>("# Votes", 7, r -> String.format("%d", r.boardGame().playerCountTotalVoteCount())),
                    new Column<>("Best With", 9, r -> r.votingPercentage().bestVotes()),
                    new Column<>("Recommend", 9, r -> r.votingPercentage().recommendedVotes()),
                    new Column<>("Not Rec.", 9, r -> r.votingPercentage().notRecommendedVotes()),
                    new Column<>("Owners", 40, r -> "%-40s".formatted(r.owners()))
                )
            )
        ).toText();
    }

    private record Ranking(BoardGame boardGame, String owners, VotingPercentage votingPercentage) {
        public Ranking(BoardGame boardGame, String owners, int playerCount) {
            this(boardGame, owners, new VotingPercentage(boardGame.playerCountPoll().individualVotes(playerCount)));
        }
    }

    private record VotingPercentage(ImmutableList<VotesPercentage> playerCountPoll, Score percentage) {
        private VotingPercentage(ImmutableList<VotesPercentage> playerCountPoll) {
            this(playerCountPoll, Score.percentage());
        }

        public String bestVotes() {
            return formatVotes(VotesPercentage::isBest);
        }

        public String recommendedVotes() {
            return formatVotes(VotesPercentage::isRecommended);
        }

        public String notRecommendedVotes() {
            return formatVotes(VotesPercentage::isNotRecommended);
        }

        private String formatVotes(Predicate<VotesPercentage> predicate) {
            return percentage.apply(playerCountPoll.detectOptional(predicate).map(VotesPercentage::percentage).map(d -> d * 100).orElse(0.0));
        }
    }
}
