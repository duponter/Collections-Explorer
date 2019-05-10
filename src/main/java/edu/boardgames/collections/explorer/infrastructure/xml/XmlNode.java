package edu.boardgames.collections.explorer.infrastructure.xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public abstract class XmlNode {
	private final XPath xpath = XPathFactory.newInstance().newXPath();
	private final Node node;

	public static Stream<Node> nodes(Node root, String expression) {
		XmlNode rootNode = new XmlNode(root) {
		};
		return rootNode.nodes(expression);
	}

	protected XmlNode(Node node) {
		this.node = Objects.requireNonNull(node);
	}

	protected String stringValueAttribute(String attribute) {
		return this.string(String.format("%s/@value", attribute));
	}

	protected String string(String expression) {
		return (String) this.data(expression, XPathConstants.STRING);
	}

	protected Number numericValueAttribute(String attribute) {
		return this.number(String.format("%s/@value", attribute));
	}

	protected Number number(String expression) {
		return (Number) this.data(expression, XPathConstants.NUMBER);
	}

	protected Stream<Node> nodes(String expression) {
		NodeList nodeList = (NodeList) this.data(expression, XPathConstants.NODESET);
		return IntStream.range(0, nodeList.getLength()).mapToObj(nodeList::item);
	}

	protected Object data(String expression, QName returnType) {
		try {
			return xpath.evaluate(expression, node, returnType);
		} catch (XPathExpressionException e) {
			throw new IllegalStateException(e);
		}
	}
}
