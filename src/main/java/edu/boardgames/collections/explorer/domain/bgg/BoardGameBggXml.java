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
	private final String id;

	public BoardGameBggXml(Node node, String id) {
		this.node = Objects.requireNonNull(node);
		this.id = Objects.requireNonNull(id);
	}

	@Override
	public String name() {
		return attributeValue("name[@type='primary']");
	}

	private String attributeValue(String attribute) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = String.format("//item[@id='%s']/%s/@value", id, attribute);
		try {
			return (String) xpath.evaluate(expression, node, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String year() {
		return attributeValue("yearpublished");
	}

	@Override
	public Range<String> playerCount() {
		return Range.of(attributeValue("minplayers"), attributeValue("maxplayers"));
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
		return Range.of(attributeValue("minplaytime"), attributeValue("maxplaytime"));
	}
}
