package edu.boardgames.collections.explorer.infrastructure.xml;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public record XslStylesheet(String location) {
    private static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String XSL_DIRECTIVE = "<?xml-stylesheet type=\"text/xsl\" href=\"%s\"?>";

    public String includeInXml(String xml) {
        return String.join(
                System.lineSeparator(),
                XML_DECLARATION,
                XSL_DIRECTIVE.formatted(location),
                StringUtils.removeStartIgnoreCase(xml, XML_DECLARATION)
        );
    }

    public String read() {
        String filename = this.asFilename();
        try {
            return Files.readString(Path.of(Objects.requireNonNull(XslStylesheet.class.getResource(filename)).toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("Unable to read Xsl Stylesheet " + filename, e);
        }
    }

    private String asFilename() {
        return StringUtils.substringBeforeLast(this.location, "/") + ".xsl";
    }
}
