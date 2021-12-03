package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.boardgames.collections.explorer.infrastructure.Async;

public class ThingRequest extends BggRequest<ThingRequest> {
	private static final Logger LOGGER = System.getLogger(ThingRequest.class.getName());

	/*
	Handle with https://jodah.net/failsafe/fallback/ ?
	414 Request-URI Too Large
	nginx/1.17.9
	max 8k = 8*1024 = 8192 chars

	min request= https://www.boardgamegeek.com/xmlapi2/thing?stats=1&id=&type=boardgame

	objectid = 6 chars
	separator = %2C

	 */
	private static final int IDS_PER_REQUEST = 900;
	private List<String> ids;

	public ThingRequest() {
		super(BggApi.V2.create("thing"));
		this.addOption("type", "boardgame");
	}

	@Override
	ThingRequest self() {
		return this;
	}

	public ThingRequest forIds(List<String> ids) {
		this.ids = ids;
		return this;
	}

	private ThingRequest addIds() {
		return this.addOption("id", String.join(",", ids));
	}

	public ThingRequest withStats() {
		return this.enableOption("stats");
	}

	@Override
	public InputStream asInputStream() {
		return this.asInputStreams()
		           .reduce(ThingRequest::onlyOne)
		           .orElseThrow(() -> new NoSuchElementException("Exactly one element expected."));
	}

	private static <T> T onlyOne(T first, T second) {
		throw new IllegalArgumentException("Only one element expected.");
	}

	public Stream<InputStream> asInputStreams() {
		return Async.map(this.splitLargeRequests(), ThingRequest::superAsInputStream);
	}

	private InputStream superAsInputStream() {
		return super.asInputStream();
	}

	@Override
	public Stream<String> asLines() {
		return Async.map(this.splitLargeRequests(), ThingRequest::superAsLines)
		            .flatMap(Function.identity());
	}

	private Stream<String> superAsLines() {
		return super.asLines();
	}

	private Stream<ThingRequest> splitLargeRequests() {
		int requestCount = (ids.size() / IDS_PER_REQUEST) + (ids.size() % IDS_PER_REQUEST > 0 ? 1 : 0);
		LOGGER.log(Level.INFO, "Given max %d ids per request, %d ids result into %d request(s)".formatted(IDS_PER_REQUEST, ids.size(), requestCount));
		if (requestCount == 0) {
			return Stream.empty();
		} else if (requestCount == 1) {
			return Stream.of(this.addIds());
		} else {
			int idCountPerRequest = (ids.size() / requestCount) + 1;
			LOGGER.log(Level.INFO, "Performing %d requests for %d ids each".formatted(requestCount, idCountPerRequest));
			return io.vavr.collection.Stream.ofAll(ids)
			                                .grouped(idCountPerRequest)
			                                .map(idsPerRequest -> this.copy(ThingRequest::new)
			                                                          .forIds(idsPerRequest.toJavaList())
			                                                          .addIds())
			                                .toJavaStream();
		}
	}
}
