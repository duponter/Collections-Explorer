package edu.boardgames.collections.explorer.infrastructure.xml;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public final class XmlInput {
	public Node read(URI uri) {
		try {
			return newDocumentBuilder().parse(uri.toString());
		} catch (SAXException | IOException e) {
			throw new IllegalArgumentException(String.format("Unable to parse XML from %s", uri), e);
		}
	}

	public Node read(InputStream inputStream) {
		try {
			return newDocumentBuilder().parse(inputStream);
		} catch (SAXException | IOException e) {
			throw new IllegalArgumentException("Unable to parse XML from InputStream", e);
		}
	}

	private DocumentBuilder newDocumentBuilder() {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}
}
