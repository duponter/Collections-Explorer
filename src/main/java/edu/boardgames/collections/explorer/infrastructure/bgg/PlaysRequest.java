package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import edu.boardgames.collections.explorer.infrastructure.Async;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.slf4j.Logger;
import org.w3c.dom.Node;

import static org.slf4j.LoggerFactory.getLogger;

public class PlaysRequest extends BggRequest<PlaysRequest> {
	private static final Logger LOGGER = getLogger(PlaysRequest.class.getName());
	private int page = 0;

	public PlaysRequest() {
		super(BggApi.V2.create("plays"));
		this.addOption("type", "thing");
	}

	@Override
	PlaysRequest self() {
		return this;
	}

	public PlaysRequest username(String username) {
		this.addOption("username", username);
		return this;
	}

	public PlaysRequest id(String id) {
		this.addOption("id", id);
		return this;
	}

	public PlaysRequest page(int page) {
		LOGGER.info("Setting page from {} to {}", this.page, page);
		this.page = page;
		this.addOption("page", Integer.toString(page));
		return this;
	}

	@Override
	public InputStream asInputStream() {
		return this.asInputStreams()
		           .reduce(PlaysRequest::onlyOne)
		           .orElseThrow(() -> new NoSuchElementException("Exactly one element expected."));
	}

	private static <T> T onlyOne(T first, T second) {
		throw new IllegalArgumentException("Only one element expected.");
	}

	public Stream<InputStream> asInputStreams() {
		return Async.map(this.createPagedRequests(), PlaysRequest::superAsInputStream);
	}

	private InputStream superAsInputStream() {
		return super.asInputStream();
	}

	@Override
	public Stream<String> asLines() {
		return Async.map(this.createPagedRequests(), PlaysRequest::superAsLines)
		            .flatMap(Function.identity());
	}

	private Stream<String> superAsLines() {
		return super.asLines();
	}

	private Stream<PlaysRequest> createPagedRequests() {
		if (this.page > 0) {
			LOGGER.info("Page {} defined, returning this request", this.page);
			return Stream.of(this);
		} else {
			LOGGER.info("No page defined, fetching total number of plays");
			Integer total = XmlNode.nodes(new XmlInput().read(this.superAsInputStream()), "/plays")
			                         .findFirst()
			                         .map(TotalPlaysBggXml::new)
			                         .map(TotalPlaysBggXml::total)
			                         .orElse(0);
			int pages = (total / 100) + 1;
			LOGGER.info("Performing {} paged requests to fetch {} plays", pages, total);
			return IntStream.rangeClosed(1, pages).mapToObj(this.copy(PlaysRequest::new)::page);
		}
	}

	private static class TotalPlaysBggXml extends XmlNode {
		TotalPlaysBggXml(Node node) {
			super(node);
		}

		public int total() {
			return super.number("@total").intValue();
		}
	}
}
