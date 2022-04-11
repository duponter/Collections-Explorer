package edu.boardgames.collections.explorer;

import java.io.Serializable;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import edu.boardgames.collections.explorer.domain.CollectedBoardGame;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.infrastructure.Async;
import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;
import edu.boardgames.collections.explorer.infrastructure.bgg.CollectionEndpoint;
import edu.boardgames.collections.explorer.ui.input.BestWithInput;
import edu.boardgames.collections.explorer.ui.input.CollectionsInput;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import static java.util.Map.entry;

@Path("/poc")
public class PocResource {
    private static final System.Logger LOGGER = System.getLogger(PocResource.class.getName());

    @GET
    @Path("/json/play")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<BoardGameJson> whatToPlay(@QueryParam("collections") String collections, @QueryParam("bestWith") Integer bestWith) {
        CollectionsInput searchableCollections = new CollectionsInput(collections);
        var bestWithInput = BestWithInput.of(bestWith);
        LOGGER.log(Level.INFO, "Search currently playable collections {0} to play a best with {1} game", searchableCollections.asText(), bestWithInput.asText());
        return searchableCollections.resolve().boardGamesDetailed()
                .stream()
                .filter(bestWithInput.resolve())
                .distinct()
                .map(BoardGameJson::new)
                .toList();
    }


    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Publisher<String> publishers() {
//        https://quarkus.io/guides/rest-json
        // Being reactive
//        You can return reactive types to handle asynchronous processing. Quarkus recommends the usage of Mutiny to write reactive and asynchronous code.
//To integrate Mutiny and RESTEasy, you need to add the quarkus-resteasy-mutiny dependency to your project:

//		List<Flowable<Pair<String, Stream<String>>>> flowables = Async.mapToFutures(users().values().stream(), toPair.andThen(pair -> mapRight(pair, nameStringExtractor))).stream().map(Flowable::fromFuture).collect(Collectors.toList());
//		return Flowable
//				.concat(flowables)
//				.flatMap(pair -> Flowable.fromIterable(pair.getRight().map(game -> String.format("%s (%s)", game, pair.getLeft())).collect(Collectors.toList())));
        return Flowable.just(BggInit.get().geekBuddies().one("duponter"))
                .map(GeekBuddy::username)
                .map(this::usernameToCollection)
                .flatMap(pair -> Flowable.fromIterable(pair.getRight().map(game -> String.format("%s (%s)", game, pair.getLeft())).toList()));
    }

    @GET
    @Path("/users")
    @Produces(MediaType.TEXT_PLAIN)
    public String infoByIds(@QueryParam("usernames") String usernames) {
//		https://www.callicoder.com/java-8-completablefuture-tutorial/
//		http://tabulator.info/
        Set<String> collectedNames = Async.map(Arrays.stream(StringUtils.split(usernames, ",")), this::usernameToCollection)
                .flatMap(pair -> pair.getRight().map(game -> String.format("%s (%s)", game, pair.getLeft())))
                .collect(Collectors.toCollection(TreeSet::new));
        return "to implement via 1. CompletableFuture.allOf() and render via http://tabulator.info/\nids:\n" + StringUtils.join(collectedNames, "\n");
    }

    private Pair<String, Stream<String>> usernameToCollection(String username) {
        return Pair.of(
                username,
                new CollectionEndpoint(username).withStats().withoutExpansions().execute()
                        .map(this::toMap)
                        .map(Map::toString)
        );
    }

    private Map<String, Serializable> toMap(CollectedBoardGame cbg) {
        return Map.ofEntries(
                entry("id", cbg.id()),
                entry("name", cbg.name()),
                entry("originalName", cbg.originalName()),
                entry("year", cbg.year()),
                entry("rated", cbg.rated()),
                entry("rating", cbg.rating()),
                entry("owned", cbg.owned()),
                entry("previouslyOwned", cbg.previouslyOwned()),
                entry("forTrade", cbg.forTrade()),
                entry("wanted", cbg.wanted()),
                entry("wantToPlay", cbg.wantToPlay()),
                entry("wantToBuy", cbg.wantToBuy()),
                entry("wishlisted", cbg.wishlisted()),
                entry("preordered", cbg.preordered()),
                entry("played", cbg.played()),
                entry("numberOfPlays", cbg.numberOfPlays()),
                entry("publicComment", cbg.publicComment())
        );
    }
}
