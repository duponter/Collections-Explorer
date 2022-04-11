package edu.boardgames.collections.explorer;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
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
import edu.boardgames.collections.explorer.ui.text.Document;
import edu.boardgames.collections.explorer.ui.text.DocumentTitle;
import edu.boardgames.collections.explorer.ui.text.Line;
import edu.boardgames.collections.explorer.ui.text.LinesParagraph;

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
		List<MageKnightSoloPlay> plays = BggInit.get().plays().forUserAndGame(username, "248562").stream()
				.map(MageKnightSoloPlay::new)
				.toList();
		Map<String, Map<String, Long>> dummyPlayerCounts = plays.stream()
				.collect(Collectors.groupingBy(
						MageKnightSoloPlay::scenario,
						Collectors.groupingBy(
								MageKnightSoloPlay::dummyPlayer,
								Collectors.counting()
						)
				));
		Map<String, Map<String, MageKnightSoloPlayAggregate>> stats = plays.stream()
				.collect(Collectors.groupingBy(
						MageKnightSoloPlay::scenario,
						Collectors.groupingBy(
								MageKnightSoloPlay::mageKnight,
								TreeMap::new,
								Collectors.collectingAndThen(
										Collectors.toList(),
										MageKnightSoloPlayAggregate::new
								)
						)
				));

		return Stream.concat(
						stats.entrySet().stream()
								.flatMap(scenarioEntry -> scenarioEntry.getValue().keySet().stream()
										.map(mageKnight -> new ScenarioMageKnight(scenarioEntry.getKey(), mageKnight))),
                        dummyPlayerCounts.entrySet().stream()
                                .flatMap(scenarioEntry -> scenarioEntry.getValue().keySet().stream()
                                        .map(dummyPlayer -> new ScenarioMageKnight(scenarioEntry.getKey(), dummyPlayer)))
                ).distinct().sorted()
                .map(smk -> new LineStats(smk.scenario(), smk.mageKnight(), stats.get(smk.scenario()).getOrDefault(smk.mageKnight(), new MageKnightSoloPlayAggregate(List.of())), dummyPlayerCounts))
                .sorted(Comparator.comparing(LineStats::scenario).thenComparing(s -> s.aggregate().stats().count()).thenComparing(s -> s.aggregate().stats().lastPlayed()))
                .map(LineStats::formatted)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private record ScenarioMageKnight(String scenario, String mageKnight) implements Comparable<ScenarioMageKnight> {
        private static final Comparator<ScenarioMageKnight> COMPARATOR = Comparator.comparing(ScenarioMageKnight::scenario)
                .thenComparing(ScenarioMageKnight::mageKnight);

        @Override
        public int compareTo(ScenarioMageKnight other) {
            return COMPARATOR.compare(this, other);
        }
    }

    private record LineStats(String scenario, String mageKnight, MageKnightSoloPlayAggregate aggregate, Map<String, Map<String, Long>> dummyPlayerCounts) {
        private String formatted() {
            return "%-20s with %-17s : %s - %2dx dummy".formatted(scenario, StringUtils.defaultIfEmpty(mageKnight, "<unknown>"), aggregate.stats().formatted(), dummyPlayerCounts.get(scenario).getOrDefault(mageKnight, 0L));
        }
    }
}
