package edu.boardgames.collections.explorer.infrastructure.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public final class XmlInput {
	public Node read(URI uri) {
		try {
			return newDocumentBuilder().parse(uri.toString());
		} catch (SAXException | IOException e) {
			throw new IllegalArgumentException("Unable to parse XML from " + uri, e);
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
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            return factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}
}
