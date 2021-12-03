package edu.boardgames.collections.explorer;

import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.domain.PlayerCount;
import edu.boardgames.collections.explorer.domain.poll.Filter;
import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;
import edu.boardgames.collections.explorer.ui.input.OwnedBoardGameFormatInput;
import edu.boardgames.collections.explorer.ui.text.Chapter;
import edu.boardgames.collections.explorer.ui.text.ChapterTitle;
import edu.boardgames.collections.explorer.ui.text.Document;
import edu.boardgames.collections.explorer.ui.text.DocumentTitle;
import edu.boardgames.collections.explorer.ui.text.Line;
import edu.boardgames.collections.explorer.ui.text.LinesParagraph;
import edu.boardgames.collections.explorer.ui.text.format.OwnedBoardGameFormat;

@Path("/shelves")
public class ShelvesResource {
	private static final System.Logger LOGGER = System.getLogger(ShelvesResource.class.getName());

	@GET
	@Path("/play")
	@Produces(MediaType.TEXT_PLAIN)
	public String current(@QueryParam("collections") String collections, @QueryParam("bestWith") Integer bestWith) {
		String[] collectionNames = StringUtils.split(collections, ",");
		LOGGER.log(Level.INFO, "Search currently playable collections {0} to play a best with {1} game", Arrays.toString(collectionNames), ObjectUtils.defaultIfNull(bestWith, "n/a"));
		List<Line> boardGames = BggInit.get().collections().withNames(collectionNames).copiesPerBoardGame()
				.entrySet().stream()
				.filter(entry -> bestWithFilter(bestWith).test(entry.getKey()))
				.map(entry -> OwnedBoardGameFormat.FULL.apply(entry.getKey(), entry.getValue()))
				.sorted()
				.map(Line::of)
				.toList();
		return new Document(
				new DocumentTitle("Search collections %s to play a best with %s game".formatted(Arrays.toString(collectionNames), ObjectUtils.defaultIfNull(bestWith, "n/a"))),
				new LinesParagraph(boardGames)
		).toText();
	}

	@GET
	@Path("/play/{geekbuddy}")
	@Produces(MediaType.TEXT_PLAIN)
	public String current(@PathParam("geekbuddy") String geekbuddy, @QueryParam("collections") String collections, @QueryParam("bestWith") Integer bestWith, @QueryParam("format") String format) {
		// TODO_EDU parameters to search criteria
		String[] collectionNames = StringUtils.split(collections, ",");
		OwnedBoardGameFormatInput formatInput = new OwnedBoardGameFormatInput(format);
		String documentTitle = "Search collections %s to play a best with %s game, tailored for %s, %s".formatted(Arrays.toString(collectionNames), ObjectUtils.defaultIfNull(bestWith, "n/a"), geekbuddy, formatInput.asText());
		LOGGER.log(Level.INFO, documentTitle);

		GeekBuddy buddy = BggInit.get().geekBuddies().one(geekbuddy);
		List<BoardGame> wantToPlay = buddy.wantToPlayCollection();
		List<BoardGame> played = buddy.ratedCollection(5);
		List<BoardGame> topRated = buddy.ratedCollection(8);
		Map<String, Collection<PlayableCopy>> boardGames = BggInit.get().collections().withNames(collectionNames).copiesPerBoardGame()
				.entrySet().stream()
				.filter(entry -> bestWithFilter(bestWith).test(entry.getKey()))
				.map(entry -> new PlayableCopy(group(entry.getKey(), wantToPlay, played, topRated), entry.getKey(), entry.getValue()))
				.collect(Collectors.groupingBy(PlayableCopy::group, Collectors.toCollection(TreeSet::new)));

		OwnedBoardGameFormat outputFormat = formatInput.resolve();
		return new Document(
				new DocumentTitle(documentTitle),
				boardGames.entrySet().stream()
						.map(entry -> new Chapter(new ChapterTitle(entry.getKey()), new LinesParagraph(entry.getValue().stream().map(pc -> pc.asLine(outputFormat)).toList())))
						.toArray(Chapter[]::new)
		).toText();
	}

	private record PlayableCopy(String group, BoardGame boardGame, Set<String> owners) implements Comparable<PlayableCopy> {
		private static final Comparator<PlayableCopy> COMPARATOR = Comparator.<PlayableCopy, String>comparing(pc -> pc.boardGame().name())
				.thenComparing(pc -> pc.boardGame().year());

		public Line asLine(OwnedBoardGameFormat format) {
			return () -> format.apply(this.boardGame(), this.owners());
		}

		@Override
		public int compareTo(PlayableCopy pc) {
			return COMPARATOR.compare(this, pc);
		}
	}

	private String group(BoardGame boardGame, List<BoardGame> wantToPlay, List<BoardGame> played, List<BoardGame> rated) {
		if (wantToPlay.contains(boardGame)) {
			return played.contains(boardGame) ? "Want to play again (long time ago or to give another chance)" : "Want to play (never played)";
		}
		if (rated.contains(boardGame)) {
			return "Want to play again (rated 8 or higher)";
		}
		return "Other";
	}

	@GET
	@Path("/wanttoplay/{geekbuddy}")
	@Produces(MediaType.TEXT_PLAIN)
	public String wantToPlay(@PathParam("geekbuddy") String geekbuddy, @QueryParam("bestWith") Integer bestWith, @QueryParam("includeRated") Integer minimallyRated) {
		LOGGER.log(Level.INFO, "Search collections of all geekbuddies for {0}'s want-to-play best with {1,number,integer} games", geekbuddy, bestWith);

		GeekBuddy buddy = BggInit.get().geekBuddies().one(geekbuddy);
		List<BoardGame> wantToPlay = buddy.wantToPlayCollection();
		LOGGER.log(Level.INFO, "Collection fetched: {0} wants to play {1,number,integer} boardgames.", geekbuddy, wantToPlay.size());
		if (minimallyRated != null) {
			List<BoardGame> rated = buddy.ratedCollection(minimallyRated);
			LOGGER.log(Level.INFO, "Collection fetched: {0} rated {1,number,integer} boardgames {1,number,integer} or more.", geekbuddy, rated.size(), minimallyRated);
			wantToPlay = Stream.concat(wantToPlay.stream(), rated.stream()).toList();
		}
		Predicate<BoardGame> wantsToPlay = wantToPlay::contains;

		List<Line> copies = BggInit.get().collections().all().copiesPerBoardGame()
				.entrySet().stream()
				.filter(entry -> bestWithFilter(bestWith).and(wantsToPlay).test(entry.getKey()))
				.map(entry -> OwnedBoardGameFormat.FULL.apply(entry.getKey(), entry.getValue()))
				.sorted()
				.map(Line::of)
				.toList();
		LOGGER.log(Level.INFO, "All owned collections matched against want to play collection");

		return new Document(
				new DocumentTitle("Search collections of all geekbuddies for %s's want-to-play best with %d games".formatted(geekbuddy, bestWith)),
				new LinesParagraph(copies)
		).toText();
	}

	@GET
	@Path("/wanttoreplay/{geekbuddy}")
	@Produces(MediaType.TEXT_PLAIN)
	public String wantToReplay(@PathParam("geekbuddy") String geekbuddy, @QueryParam("bestWith") Integer bestWith, @QueryParam("minimallyRated") Integer minimallyRated) {
		LOGGER.log(Level.INFO, "{0} wants to replay best with {1,number,integer} games minimally rated {2,number,integer}", geekbuddy, bestWith, minimallyRated);

		GeekBuddy buddy = BggInit.get().geekBuddies().one(geekbuddy);
		List<BoardGame> rated = buddy.ratedCollection(minimallyRated);
		LOGGER.log(Level.INFO, "Collection fetched: {0} rated {1,number,integer} boardgames {1,number,integer} or more.", geekbuddy, rated.size(), minimallyRated);

		return new Document(
				new DocumentTitle("Show replay options for %s's want-to-play best with %d games rated at least %d".formatted(geekbuddy, bestWith, minimallyRated)),
				new LinesParagraph(rated.stream().map(bg -> OwnedBoardGameFormat.SLIM.apply(bg, Set.of())).map(Line::of).toList())
		).toText();
	}

	@GET
	@Path("/lookup/{bgId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String lookup(@PathParam("bgId") String boardGameId) {
		List<BoardGame> boardGames = BggInit.get().boardGames().withIds(Stream.of(boardGameId));
		if (boardGames.isEmpty()) {
			return String.format("Board Game with id [%s] not found or does not exists on BGG", boardGameId);
		}

		String boardGameName = boardGames.stream().findFirst().map(BoardGame::name).orElseThrow();
		LOGGER.log(Level.INFO, "Searching all collections for game {0}", boardGameName);
		String copies = BggInit.get().collections().all().copiesPerBoardGame()
				.entrySet().stream()
				.filter(entry -> StringUtils.equals(entry.getKey().id(), boardGameId))
				.findFirst()
				.map(entry -> OwnedBoardGameFormat.FULL.apply(entry.getKey(), entry.getValue()))
				.orElse(">>> not found in known collections");
		return String.format("Searched all known collections for game %s:%n%n%s", boardGameName, copies);
	}

	private Filter<BoardGame> bestWithFilter(Integer bestWith) {
		return bestWith != null ? new PlayerCount(bestWith)::bestOnly : bg -> true;
	}
}
