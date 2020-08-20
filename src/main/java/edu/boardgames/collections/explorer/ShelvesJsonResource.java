package edu.boardgames.collections.explorer;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.Copy;
import edu.boardgames.collections.explorer.domain.PlayerCount;
import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/shelvesjson")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShelvesJsonResource {
	private static final System.Logger LOGGER = System.getLogger(ShelvesJsonResource.class.getName());

	@GET
	@Path("/play")
	public List<BoardGameJson> whatToPlay(@QueryParam("collections") String collections, @QueryParam("bestWith") Integer bestWith) {
		String[] collectionNames = StringUtils.split(collections, ",");
		LOGGER.log(Level.INFO, "Search currently playable collections {0} to play a best with {1} game", Arrays.toString(collectionNames), ObjectUtils.defaultIfNull(bestWith, "n/a"));
		Map<BoardGame, Set<String>> boardGames = copiesPerBoardGame(
				BggInit.get().collections().withNames(collectionNames),
				toCopyFilter(bestWithFilter(bestWith))
		);
		return boardGames.keySet().stream().map(BoardGameJson::new).collect(Collectors.toList());
	}

	private Predicate<Copy> toCopyFilter(Predicate<BoardGame> filter) {
		return lift(filter, Copy::boardGame);
	}

	private <T, R> Predicate<R> lift(Predicate<T> filter, Function<R, T> mapper) {
		return subject -> filter.test(mapper.apply(subject));
	}

	private Predicate<BoardGame> bestWithFilter(Integer bestWith) {
		return bestWith != null ? new PlayerCount(bestWith)::bestOnly : bg -> true;
	}

	private Map<BoardGame, Set<String>> copiesPerBoardGame(BoardGameCollection collection, Predicate<Copy> filter) {
		return collection.boardGameCopies()
		                 .stream()
		                 .filter(filter)
		                 .collect(Collectors.groupingBy(Copy::boardGame, Collectors.mapping(Copy::collection, Collectors.mapping(BoardGameCollection::name, Collectors.toCollection(TreeSet::new)))));
	}
}
