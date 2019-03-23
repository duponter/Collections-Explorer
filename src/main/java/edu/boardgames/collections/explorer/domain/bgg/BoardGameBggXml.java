package edu.boardgames.collections.explorer.domain.bgg;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.Range;
import org.w3c.dom.Node;

import java.util.Objects;
import java.util.Optional;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class BoardGameBggXml implements BoardGame {
	private final Node node;

	public BoardGameBggXml(Node node) {
		this.node = Objects.requireNonNull(node);
	}

	@Override
	public String name() {
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "//item[@id='245931']/name/@value";
		try {
			return (String) xpath.evaluate(expression, node, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String year() {
		return "2016";
	}

	@Override
	public Range<String> playerCount() {
		return Range.of("2", "2");
	}

	@Override
	public Optional<Range<Integer>> bestWithPlayerCount() {
			return Optional.empty();
	}

	@Override
	public Optional<Range<Integer>> recommendedWithPlayerCount() {
			return Optional.empty();
	}

	@Override
	public Range<String> playtime() {
		return Range.of("60", "60");
	}
}
