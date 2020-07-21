package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.infrastructure.Async;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.w3c.dom.Node;

import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PlaysRequest extends BggRequest<PlaysRequest> {
	private static final Logger LOGGER = System.getLogger(PlaysRequest.class.getName());

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
			LOGGER.log(Level.DEBUG, "Page {0, number, integer} defined, returning this request", this.page);
			return Stream.of(this);
		} else {
			LOGGER.log(Level.DEBUG, "No page defined, fetching total number of plays", this.page);
			Integer total = XmlNode.nodes(new XmlInput().read(this.superAsInputStream()), "/plays")
			                         .findFirst()
			                         .map(TotalPlaysBggXml::new)
			                         .map(TotalPlaysBggXml::total)
			                         .orElse(0);
			int pages = (total / 100) + 1;
			LOGGER.log(Level.DEBUG, "Performing {0, number, integer} paged requests to fetch {1, number, integer} plays", pages, total);
			return IntStream.rangeClosed(1, pages)
					.mapToObj(page -> this.copy(PlaysRequest::new).page(page));
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
