package edu.boardgames.collections.explorer.domain.bgg;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.Range;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Objects;
import java.util.Optional;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class BoardGameBggXml implements BoardGame {
	private final Node node;
	private final XPath xpath = XPathFactory.newInstance().newXPath();

	public BoardGameBggXml(Node node) {
		this.node = Objects.requireNonNull(node);
	}

	@Override
	public String name() {
		return stringValueAttribute("name[@type='primary']");
	}

	@Override
	public String year() {
		return stringValueAttribute("yearpublished");
	}

	@Override
	public Double bggScore() {
		return numericValueAttribute("statistics/ratings/average").doubleValue();
	}

	@Override
	public Range<String> playerCount() {
		return Range.of(stringValueAttribute("minplayers"), stringValueAttribute("maxplayers"));
	}

	@Override
	public Optional<Range<Integer>> bestWithPlayerCount() {
		return Optional.empty();
	}

	@Override
	public Optional<Range<Integer>> recommendedWithPlayerCount() {
		NodeList results = (NodeList) this.attributeValue("poll[@name='suggested_numplayers']/results", XPathConstants.NODESET);
		System.out.printf("recommendedWithPlayerCount: %d%n", results.getLength());
		return Optional.empty();
	}

	@Override
	public Range<String> playtime() {
		return Range.of(stringValueAttribute("minplaytime"), stringValueAttribute("maxplaytime"));
	}

	@Override
	public Double weight() {
		return numericValueAttribute("statistics/ratings/averageweight").doubleValue();
	}

	private String stringValueAttribute(String attribute) {
		return (String) this.attributeValue(String.format("%s/@value", attribute), XPathConstants.STRING);
	}

	private Number numericValueAttribute(String attribute) {
		return (Number) this.attributeValue(String.format("%s/@value", attribute), XPathConstants.NUMBER);
	}

	private Object attributeValue(String expression, QName returnType) {
		try {
			return xpath.evaluate(expression, node, returnType);
		} catch (XPathExpressionException e) {
			throw new IllegalStateException(e);
		}
	}
}
