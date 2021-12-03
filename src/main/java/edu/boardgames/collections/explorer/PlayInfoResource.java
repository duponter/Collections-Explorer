package edu.boardgames.collections.explorer;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import edu.boardgames.collections.explorer.domain.MageKnightSoloPlay;
import edu.boardgames.collections.explorer.domain.MageKnightSoloPlayAggregate;
import edu.boardgames.collections.explorer.domain.Play;
import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;
import edu.boardgames.collections.explorer.infrastructure.bgg.PlayBggXml;
import edu.boardgames.collections.explorer.infrastructure.bgg.PlaysRequest;
import edu.boardgames.collections.explorer.infrastructure.bgg.ThingRequest;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import edu.boardgames.collections.explorer.ui.text.Chapter;
import edu.boardgames.collections.explorer.ui.text.ChapterTitle;
import edu.boardgames.collections.explorer.ui.text.Document;
import edu.boardgames.collections.explorer.ui.text.DocumentTitle;
import edu.boardgames.collections.explorer.ui.text.Line;
import edu.boardgames.collections.explorer.ui.text.LinesParagraph;
import io.vavr.Tuple;

@Path("/playinfo")
public class PlayInfoResource {
	private static final Logger LOGGER = System.getLogger(PlayInfoResource.class.getName());

	@GET
	@Path("/xsl")
	@Produces(MediaType.TEXT_XML)
	public String xsl() throws URISyntaxException, IOException {
		return Files.readString(java.nio.file.Path.of(PlayInfoResource.class.getResource("play-info.xsl").toURI()));
	}

	@GET
	@Path("/formatted/{id}")
	@Produces(MediaType.TEXT_XML)
	public String xml(@PathParam("id") String id) {
		String response = new ThingRequest().withStats().forIds(Arrays.asList(id.split("\\s*,\\s*"))).asLines().collect(Collectors.joining());

		String xsl = """
				<?xml version="1.0" encoding="UTF-8"?>
				<?xml-stylesheet type="text/xsl" href="http://localhost:8080/playinfo/xsl"?>
				""";
		String result = String.format("%s%n%s", xsl, StringUtils.removeStartIgnoreCase(response, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		LOGGER.log(Level.INFO, result);
		return result;
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public String infoByIds(@PathParam("id") String id) {
		return BggInit.get().boardGames().withIds(Stream.of(id)).stream()
				.map(BoardGameRender::playInfo)
				.collect(Collectors.joining(System.lineSeparator()));
	}

	@GET
	@Path("/plays/{username}/mksolo")
	@Produces(MediaType.TEXT_PLAIN)
	public String mageKnightSoloPlays(@PathParam("username") String username) {
		List<MageKnightSoloPlay> plays = new PlaysRequest().username(username).id("248562")
				.asInputStreams()
				.map(new XmlInput()::read)
				.flatMap(root -> XmlNode.nodes(root, "//play"))
				.map(PlayBggXml::new)
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
								.map(mageKnight -> Tuple.of(scenarioEntry.getKey(), mageKnight))),
				dummyPlayerCounts.entrySet().stream()
						.flatMap(scenarioEntry -> scenarioEntry.getValue().keySet().stream()
								.map(dummyPlayer -> Tuple.of(scenarioEntry.getKey(), dummyPlayer)))
		).distinct().sorted()
				.map(tuple -> new LineStats(tuple._1(), tuple._2(), stats.get(tuple._1()).getOrDefault(tuple._2(), new MageKnightSoloPlayAggregate(List.of())), dummyPlayerCounts))
				.sorted(Comparator.comparing(LineStats::scenario).thenComparing(s -> s.aggregate().stats().count()).thenComparing(s -> s.aggregate().stats().lastPlayed()))
				.map(LineStats::formatted)
				.collect(Collectors.joining(System.lineSeparator()));
	}

	private record LineStats(String scenario, String mageKnight, MageKnightSoloPlayAggregate aggregate, Map<String, Map<String, Long>> dummyPlayerCounts) {
		private String formatted() {
			return "%-20s with %-17s : %s - %2dx dummy".formatted(scenario, StringUtils.defaultIfEmpty(mageKnight, "<unknown>"), aggregate.stats().formatted(), dummyPlayerCounts.get(scenario).getOrDefault(mageKnight, 0L));
		}
	}

	@GET
	@Path("/plays/{username}")
	@Produces(MediaType.TEXT_PLAIN)
	public String userPlays(@PathParam("username") String username) {
		List<PlayStats> stats = new PlaysRequest().username(username)
				.asInputStreams()
				.map(new XmlInput()::read)
				.flatMap(root -> XmlNode.nodes(root, "//play"))
				.map(PlayBggXml::new)
				.collect(Collectors.groupingBy(Play::boardGameId, new PlayStatsCollector()))
				.values().stream()
				.sorted(Comparator.reverseOrder())
				.toList();
		return new Document(
				new DocumentTitle("Plays of %s".formatted(username)),
				new Chapter(
						new ChapterTitle(String.join("\t", StringUtils.rightPad("Game", 10), StringUtils.center("Last", 10), "Times")),
						new LinesParagraph(
								stats.stream()
										.map(stat -> Line.of(
												String.join("\t",
														"%-10s".formatted(stat.boardGameId),
														"%s".formatted(stat.lastPlayed),
														"%5d".formatted(stat.timesPlayed)
												)
										)).toList()
						)
				)
		).toText();
	}

	private static class PlayStats implements Comparable<PlayStats> {
		private String boardGameId;
		private LocalDate lastPlayed;
		private int timesPlayed;

		private PlayStats init(Play play) {
			this.boardGameId = play.boardGameId();
			this.timesPlayed += play.quantity();
			if (this.lastPlayed == null || play.date().isAfter(this.lastPlayed)) {
				this.lastPlayed = play.date();
			}
			return this;
		}

		@Override
		public int compareTo(PlayStats o) {
			return this.lastPlayed.compareTo(o.lastPlayed);
		}
	}

	private static class PlayStatsCollector implements Collector<Play, PlayStats, PlayStats> {
		@Override
		public Supplier<PlayStats> supplier() {
			return PlayStats::new;
		}

		@Override
		public BiConsumer<PlayStats, Play> accumulator() {
			return PlayStats::init;
		}

		@Override
		public BinaryOperator<PlayStats> combiner() {
			return (current, other) -> processNext(current, other.lastPlayed, other.timesPlayed);
		}

		private PlayStats processNext(PlayStats stats, LocalDate lastPlayed, int timesPlayed) {
			stats.timesPlayed += timesPlayed;
			if (stats.lastPlayed == null || lastPlayed.isAfter(stats.lastPlayed)) {
				stats.lastPlayed = lastPlayed;
			}
			return stats;
		}

		@Override
		public Function<PlayStats, PlayStats> finisher() {
			return Function.identity();
		}

		@Override
		public Set<Characteristics> characteristics() {
			return EnumSet.of(Collector.Characteristics.IDENTITY_FINISH);
		}
	}
}
