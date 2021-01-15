package edu.boardgames.collections.explorer;

import java.lang.System.Logger;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.Copy;
import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;

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
		String boardGames = displayMultiLineString(BggInit.get().collections().withNames(collectionNames), playerCount);
		return String.format("%s%n%s", String.join(";", "Boardgame", "Players", "Playtime", "Weight", "BGG Score", "# Votes", "Best With", "Recommended", "Not Recommended", "Owners"), boardGames);
	}

	private String displayMultiLineString(BoardGameCollection collection, Integer playerCount) {
		return collection.boardGameCopies().stream().collect(Collectors.groupingBy(Copy::boardGame, Collectors.mapping(Copy::collection, Collectors.mapping(BoardGameCollection::name, Collectors.toCollection(TreeSet::new)))))
				.entrySet()
				.stream()
				.map(entry -> BoardGameRender.ranking(entry.getKey(), String.join(", ", entry.getValue()), playerCount))
				.sorted()
				.collect(Collectors.joining("\n"));
	}
}
