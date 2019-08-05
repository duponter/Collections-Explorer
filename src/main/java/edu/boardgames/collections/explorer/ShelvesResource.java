package edu.boardgames.collections.explorer;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
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
		return String.format("Search collections of %s to play a best with %d game", Arrays.toString(usernames), bestWith);
	}

	@GET
	@Path("/lookup/{bgId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String lookup(@PathParam("bgId") String boardGameId) {
		LOGGER.info("Search collections of all geekbuddies for game {}", boardGameId);
		return String.format("Search collections of all geekbuddies for game %s", boardGameId);
	}
}