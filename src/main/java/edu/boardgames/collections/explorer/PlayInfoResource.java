package edu.boardgames.collections.explorer;

import edu.boardgames.collections.explorer.domain.Play;
import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;
import edu.boardgames.collections.explorer.infrastructure.bgg.PlayBggXml;
import edu.boardgames.collections.explorer.infrastructure.bgg.PlaysRequest;
import edu.boardgames.collections.explorer.infrastructure.bgg.ThingRequest;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
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

		String xsl = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<?xml-stylesheet type=\"text/xsl\" href=\"http://localhost:8080/playinfo/xsl\"?>\n";
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
	@Path("/plays/{username}")
	@Produces(MediaType.TEXT_PLAIN)
	public String userPlays(@PathParam("username") String username) {
		return new PlaysRequest().username(username)
		                         .asInputStreams()
		                         .map(new XmlInput()::read)
		                         .flatMap(root -> XmlNode.nodes(root, "//play"))
		                         .map(PlayBggXml::new)
		                         .collect(Collectors.groupingBy(Play::boardGameId, new PlayStatsCollector()))
		                         .entrySet().stream()
		                         .map(play -> String.format("Game: %s - Last: %s - Times: %d", play.getKey(), play.getValue().lastPlayed, play.getValue().timesPlayed))
		                         .collect(Collectors.joining(System.lineSeparator()));
	}

	private static class PlayStats {
		private LocalDate lastPlayed;
		private int timesPlayed;
	}

	private static class PlayStatsCollector implements Collector<Play, PlayStats, PlayStats> {
		@Override
		public Supplier<PlayStats> supplier() {
			return PlayStats::new;
		}

		@Override
		public BiConsumer<PlayStats, Play> accumulator() {
			return (stats, play) -> processNext(stats, play.date(), play.quantity());
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
