package edu.boardgames.collections.explorer;

import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
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
import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;
import edu.boardgames.collections.explorer.ui.input.BestWithInput;
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
				.filter(entry -> BestWithInput.of(bestWith).resolve().test(entry.getKey()))
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
	public String current(@PathParam("geekbuddy") String geekbuddy, @QueryParam("collections") List<String> collections, @QueryParam("bestWith") Integer bestWith, @QueryParam("format") String format) {
		// TODO_EDU parameters to search criteria
		String[] collectionNames = collections.stream().flatMap(s -> Arrays.stream(StringUtils.split(s, ","))).toArray(String[]::new);
		OwnedBoardGameFormatInput formatInput = new OwnedBoardGameFormatInput(format);
		String documentTitle = "Search collections %s to play a best with %s game, tailored for %s, %s".formatted(Arrays.toString(collectionNames), ObjectUtils.defaultIfNull(bestWith, "n/a"), geekbuddy, formatInput.asText());
		LOGGER.log(Level.INFO, documentTitle);

		GeekBuddy buddy = BggInit.get().geekBuddies().one(geekbuddy);
		List<BoardGame> wantToPlay = buddy.wantToPlayCollection();
		List<BoardGame> played = buddy.ratedCollection(5);
		List<BoardGame> topRated = buddy.ratedCollection(8);
		Map<PlayGroup, Collection<PlayableCopy>> boardGames = BggInit.get().collections().withNames(collectionNames).copiesPerBoardGame()
				.entrySet().stream()
				.filter(entry -> BestWithInput.of(bestWith).resolve().test(entry.getKey()))
				.map(entry -> new PlayableCopy(group(entry.getKey(), wantToPlay, played, topRated), entry.getKey(), entry.getValue()))
				.collect(Collectors.groupingBy(PlayableCopy::group, TreeMap::new, Collectors.toCollection(TreeSet::new)));

		OwnedBoardGameFormat outputFormat = formatInput.resolve();
		return new Document(
				new DocumentTitle(documentTitle),
				boardGames.entrySet().stream()
						.map(entry -> new Chapter(new ChapterTitle(entry.getKey().title()), new LinesParagraph(entry.getValue().stream().map(pc -> pc.asLine(outputFormat)).toList())))
						.toArray(Chapter[]::new)
		).toText();
	}

	private record PlayableCopy(PlayGroup group, BoardGame boardGame, Set<String> owners) implements Comparable<PlayableCopy> {
		private static final Comparator<PlayableCopy> COMPARATOR = Comparator.<PlayableCopy, String>comparing(pc -> pc.boardGame().name())
				.thenComparing(pc -> pc.boardGame().year());

		public Line asLine(OwnedBoardGameFormat format) {
			return Line.of(format.apply(this.boardGame(), this.owners()));
		}

		@Override
		public int compareTo(PlayableCopy pc) {
			return COMPARATOR.compare(this, pc);
		}
	}

	private PlayGroup group(BoardGame boardGame, List<BoardGame> wantToPlay, List<BoardGame> played, List<BoardGame> rated) {
		if (wantToPlay.contains(boardGame)) {
			return played.contains(boardGame) ? PlayGroup.WANT_ALREADY_PLAYED : PlayGroup.WANT_NEVER_PLAYED;
		}
		if (rated.contains(boardGame)) {
			return PlayGroup.WANT_RATED_HIGH;
		}
		return played.contains(boardGame) ? PlayGroup.ALREADY_PLAYED : PlayGroup.NEVER_PLAYED;
	}

	private enum PlayGroup {
		WANT_NEVER_PLAYED("Want to play (never played)"),
		WANT_ALREADY_PLAYED("Want to play again (long time ago or to give another chance)"),
		WANT_RATED_HIGH("Want to play again (rated 8 or higher)"),
		ALREADY_PLAYED("Already played"),
		NEVER_PLAYED("Never played");

		private final String title;

		PlayGroup(String title) {
			this.title = title;
		}

		public String title() {
			return title;
		}
	}
}
