package edu.boardgames.collections.explorer;

import edu.boardgames.collections.explorer.domain.Copy;
import edu.boardgames.collections.explorer.domain.GeekBuddies;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.infrastructure.Async;
import edu.boardgames.collections.explorer.infrastructure.bgg.GeekBuddiesBggInMemory;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/shelves")
public class ShelvesResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(ShelvesResource.class);

	@GET
	@Path("/play")
	@Produces(MediaType.TEXT_PLAIN)
	public String play(@QueryParam("geekbuddies") String geekbuddies, @QueryParam("bestWith") Integer bestWithFilter) {
		String[] usernames = StringUtils.split(geekbuddies, ",");
		Integer bestWith = ObjectUtils.defaultIfNull(bestWithFilter, usernames.length);
		LOGGER.info("Search collections of {} to play a best with {} game", usernames, bestWith);
		Function<GeekBuddy, List<Copy>> owned = geekBuddy -> Copy.from(geekBuddy, geekBuddy.ownedCollection());
		String collections = Async.map(geekBuddies().withUsername(usernames).stream(), owned)
				.flatMap(List::stream)
				.collect(Collectors.groupingBy(Copy::boardGame, Collectors.mapping(Copy::owner, Collectors.mapping(GeekBuddy::name, Collectors.joining(", ")))))
				.entrySet().stream()
				.map(entry -> String.format("%s (%s) owned by %s", entry.getKey().name(), entry.getKey().year(), entry.getValue()))
				.sorted()
				.collect(Collectors.joining("\n"));
		return String.format("Search collections of %s to play a best with %d game%n%n%s", Arrays.toString(usernames), bestWith, collections);
	}

	@GET
	@Path("/lookup/{bgId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String lookup(@PathParam("bgId") String boardGameId) {
		LOGGER.info("Search collections of all geekbuddies for game {}", boardGameId);
		Function<GeekBuddy, List<Copy>> owned = geekBuddy -> Copy.from(geekBuddy, geekBuddy.ownedCollection());
		String copies = Async.map(geekBuddies().all().stream(), owned)
				.flatMap(List::stream)
				.filter(copy -> StringUtils.equals(copy.boardGame().id(), boardGameId))
				.collect(Collectors.groupingBy(Copy::boardGame, Collectors.mapping(Copy::owner, Collectors.mapping(GeekBuddy::name, Collectors.joining(", ")))))
				.entrySet().stream()
				.map(entry -> String.format("%s (%s) owned by %s", entry.getKey().name(), entry.getKey().year(), entry.getValue()))
				.sorted()
				.collect(Collectors.joining("\n"));
		return String.format("Search collections of all geekbuddies for game %s%n%n%s", boardGameId, copies);
	}

	private GeekBuddies geekBuddies() {
		return new GeekBuddiesBggInMemory();
	}
}