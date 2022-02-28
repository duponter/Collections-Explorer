package edu.boardgames.collections.explorer;

import java.lang.System.Logger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;
import edu.boardgames.collections.explorer.infrastructure.bgg.PlayerCountPoll;
import edu.boardgames.collections.explorer.ui.text.Column;
import edu.boardgames.collections.explorer.ui.text.Document;
import edu.boardgames.collections.explorer.ui.text.DocumentTitle;
import edu.boardgames.collections.explorer.ui.text.Table;
import edu.boardgames.collections.explorer.ui.text.format.Score;

import static java.lang.System.getLogger;

@Path("/ranking")
public class RankingResource {
    private static final Logger LOGGER = getLogger(RankingResource.class.getName());

    @GET
    @Path("/playercount/{count}")
    @Produces(MediaType.TEXT_PLAIN)
    public String current(@PathParam("count") Integer playerCount, @QueryParam("collections") String collections) {
        String[] collectionNames = StringUtils.split(collections, ",");
        LOGGER.log(Logger.Level.INFO, "Search currently playable collections {0} to rank games for {1} players", Arrays.toString(collectionNames), playerCount);
        return new Document(
                new DocumentTitle("Search currently playable collections %s to rank games for %s players".formatted(collections, playerCount)),
                new Table<>(
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
                        ),
                        BggInit.get().collections().withNames(collectionNames).copiesPerBoardGame()
                                .entrySet().stream()
                                .map(entry -> new Ranking(entry.getKey(), String.join(", ", entry.getValue()), playerCount))
                                .sorted(Comparator.comparing(r1 -> r1.boardGame().name())).toList()
                )
        ).toText();
    }

    private record Ranking(BoardGame boardGame, String owners, VotingPercentage votingPercentage) {
        public Ranking(BoardGame boardGame, String owners, int playerCount) {
            this(boardGame, owners, new VotingPercentage(boardGame.playerCountVotes(playerCount)));
        }
    }

    private record VotingPercentage(Optional<PlayerCountPoll> playerCountPoll, double playerCountVoteCount, Score percentage) {
        private VotingPercentage(Optional<PlayerCountPoll> playerCountPoll) {
            this(
                    playerCountPoll,
                    playerCountPoll
                            .map(poll -> poll.bestVotes() + poll.recommendedVotes() + poll.notRecommendedVotes())
                            .orElse(1) / 100.0,
                    Score.percentage());
        }

        public String bestVotes() {
            return this.displayVotes(PlayerCountPoll::bestVotes);
        }

        public String recommendedVotes() {
            return this.displayVotes(PlayerCountPoll::recommendedVotes);
        }

        public String notRecommendedVotes() {
            return this.displayVotes(PlayerCountPoll::notRecommendedVotes);
        }

        private String displayVotes(Function<PlayerCountPoll, Integer> voteSelector) {
            return percentage.apply(playerCountPoll.map(voteSelector).orElse(0).doubleValue() / playerCountVoteCount);
        }
    }
}
