package edu.boardgames.collections.explorer;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.MageKnightSoloPlay;
import edu.boardgames.collections.explorer.domain.MageKnightSoloPlayAggregate;
import edu.boardgames.collections.explorer.domain.Play;
import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;
import edu.boardgames.collections.explorer.ui.input.GeekBuddyInput;
import edu.boardgames.collections.explorer.ui.text.Chapter;
import edu.boardgames.collections.explorer.ui.text.ChapterTitle;
import edu.boardgames.collections.explorer.ui.text.Column;
import edu.boardgames.collections.explorer.ui.text.Document;
import edu.boardgames.collections.explorer.ui.text.DocumentTitle;
import edu.boardgames.collections.explorer.ui.text.Line;
import edu.boardgames.collections.explorer.ui.text.LinesParagraph;
import edu.boardgames.collections.explorer.ui.text.Table;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import static java.lang.System.Logger.Level.INFO;

@Path("/plays")
@Produces(MediaType.TEXT_PLAIN)
public class PlaysResource {
    private static final System.Logger LOGGER = System.getLogger(PlaysResource.class.getName());

    @GET
    @Path("/{username}")
    public String userPlays(@PathParam("username") String username) {
        Instant now = Instant.now();

        Map<String, List<Play>> plays = BggInit.get().plays().forUser(username).stream()
                .collect(Collectors.groupingBy(Play::boardGameId, Collectors.toList()));

        Map<String, BoardGame> boardGames = BggInit.get().boardGames().withIds(plays.keySet().stream()).stream()
                .collect(Collectors.toMap(BoardGame::id, Function.identity()));

        List<Line> stats = plays.entrySet().stream()
                .map(entry -> new BoardGamePlays(boardGames.get(entry.getKey()), entry.getValue()))
                .map(BoardGamePlays::summarize)
                .sorted(Comparator.reverseOrder())
                .map(stat -> Line.of(
                        String.join("\t",
                                "%-70s".formatted(stat.boardGame().name()),
                                stat.lastPlay().toString(),
                                "%5d".formatted(stat.count())
                        )
                )).toList();

        String response = new Document(
                new DocumentTitle("Plays of %s".formatted(username)),
                new Chapter(
                        new ChapterTitle(String.join("\t", StringUtils.rightPad("Game", 70), StringUtils.center("Last", 10), "Times")),
                        new LinesParagraph(stats)
                )
        ).toText();
        LOGGER.log(INFO, "Request took {0} to complete", Duration.between(now, Instant.now()));
        return response;
    }

    @GET
    @Path("/{username}/shelfofshame")
    public String shelfOfShame(@PathParam("username") String username) {
        Instant now = Instant.now();
        GeekBuddyInput geekbuddyInput = new GeekBuddyInput(username);

        Map<String, List<Play>> plays = BggInit.get().plays().forUser(username).stream()
                .collect(Collectors.groupingBy(Play::boardGameId, Collectors.toList()));
		/*
		2) Filter children games
		https://boardgamegeek.com/xmlapi2/collection?id=8195&type=boardgame&username=duponter&stats=1&version=1
		https://boardgamegeek.com/xmlapi2/collection?id=8195,204583&type=boardgame&username=duponter&stats=1&version=1
		<name sortindex="1">Children: Viva Topo!</name> <version> <other>Children</other> </version>
		 */
        List<BoardGamePlaySummary> stats = geekbuddyInput.resolve().ownedCollection().boardGamesDetailed().stream()
                .map(bg -> joinPlays(bg, plays))
                .map(BoardGamePlays::summarize)
                .toList();

        String response = new Document(
                new DocumentTitle("Shelf of Shame of %s".formatted(geekbuddyInput.asText())),
                new LinesParagraph(
                        stats.stream()
                                .sorted()
                                .map(stat -> Line.of(
                                        String.join("\t",
                                                "%-70s".formatted(stat.boardGame().name()),
                                                Objects.toString(stat.lastPlay(), " ".repeat(10)),
                                                "%2d".formatted(stat.count()),
                                                Objects.toString(stat.firstPlay(), " ".repeat(10))
                                        )
                                )).toList()
                )
        ).toText();
        LOGGER.log(INFO, "Request took {0} to complete", Duration.between(now, Instant.now()));
        return response;
    }

    private BoardGamePlays joinPlays(BoardGame boardGame, Map<String, List<Play>> plays) {
        return new BoardGamePlays(boardGame, Stream.concat(
                        Stream.of(boardGame),
                        boardGame.contains().stream()
                ).map(bg -> plays.getOrDefault(bg.id(), List.of()))
                .flatMap(List::stream)
                .toList());
    }

    private record BoardGamePlays(BoardGame boardGame, List<Play> plays) {
        private static final Comparator<Play> PLAY_COMPARATOR = Comparator.comparing(Play::date);

        private BoardGamePlaySummary summarize() {
            if (plays.isEmpty()) {
                return new BoardGamePlaySummary(this.boardGame());
            }
            int count = this.plays().stream().mapToInt(Play::quantity).sum();
            LocalDate firstPlay = this.plays().stream().min(PLAY_COMPARATOR).map(Play::date).orElse(null);
            LocalDate lastPlay = this.plays().stream().max(PLAY_COMPARATOR).map(Play::date).orElse(null);
            return new BoardGamePlaySummary(this.boardGame(), count, firstPlay, lastPlay);
        }
    }

    private record BoardGamePlaySummary(BoardGame boardGame, int count, LocalDate firstPlay, LocalDate lastPlay) implements Comparable<BoardGamePlaySummary> {
        private static final Comparator<BoardGamePlaySummary> COMPARATOR = Comparator.comparing(BoardGamePlaySummary::lastPlay, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(s -> s.boardGame.name());

        BoardGamePlaySummary(BoardGame boardGame) {
            this(boardGame, 0, null, null);
        }

        @Override
        public int compareTo(BoardGamePlaySummary other) {
            return COMPARATOR.compare(this, other);
        }
    }

	@GET
	@Path("/{username}/mksolo")
	public String mageKnightSoloPlays(@PathParam("username") String username) {
        ImmutableList<MageKnightSoloPlay> plays = Lists.immutable.fromStream(BggInit.get().plays().forUserAndGame(username, "248562").stream().map(MageKnightSoloPlay::new));
        return new Document(
                new DocumentTitle("Overview of %d Mage Knight Solo Plays by %s".formatted(plays.size(), new GeekBuddyInput(username).asText())),
                new LinesParagraph(
                        plays.groupBy(p -> new ScenarioMageKnight(p.scenario(), p.mageKnight()))
                                .keyMultiValuePairsView()
                                .collect(p -> new LineStats(p.getOne().scenario(), p.getOne().mageKnight(), new MageKnightSoloPlayAggregate(p.getTwo().toList())))
                                .toSortedList(Comparator.comparing(LineStats::scenario).thenComparing(s -> s.aggregate().stats().count()).thenComparing(s -> s.aggregate().stats().lastPlayed()))
                ),
                new LinesParagraph(Line.EMPTY, Line.EMPTY, Line.of("-".repeat(120)), Line.EMPTY, Line.EMPTY),
                new Table<>(
                        List.of(
                                new Column<>("Dummy Player", 25, dp -> "%-25s".formatted(dp.dummyPlayer())),
                                new Column<>("Count", 5, dp -> String.valueOf(dp.count())),
                                new Column<>("Last", 15, dp -> dp.last().toString())
                        ),
                        plays.groupBy(MageKnightSoloPlay::dummyPlayer).multiValuesView()
                                .collect(DummyPlayer::new)
                                .toSortedList(
                                        Comparator.comparing(DummyPlayer::count)
                                                .thenComparing(DummyPlayer::last)
                                                .thenComparing(DummyPlayer::dummyPlayer)
                                )
                )
        ).toText();
    }

    private record DummyPlayer(String dummyPlayer, int count, LocalDate last) {
        public DummyPlayer(RichIterable<MageKnightSoloPlay> plays) {
            this(
                    StringUtils.defaultIfEmpty(plays.minBy(MageKnightSoloPlay::dummyPlayer).dummyPlayer(), "<unknown>"),
                    plays.size(),
                    plays.maxBy(MageKnightSoloPlay::date).date()
            );
        }
    }

    private record ScenarioMageKnight(String scenario, String mageKnight) implements Comparable<ScenarioMageKnight> {
        private static final Comparator<ScenarioMageKnight> COMPARATOR = Comparator.comparing(ScenarioMageKnight::scenario)
                .thenComparing(ScenarioMageKnight::mageKnight);

        @Override
        public int compareTo(ScenarioMageKnight other) {
            return COMPARATOR.compare(this, other);
        }
    }

    private record LineStats(String scenario, String mageKnight, MageKnightSoloPlayAggregate aggregate) implements Line {
        @Override
        public String line() {
            return "%-20s with %-17s : %s".formatted(scenario, StringUtils.defaultIfEmpty(mageKnight, "<unknown>"), aggregate.stats().formatted());
        }
    }
}
