package edu.boardgames.collections.explorer.infrastructure.xml;

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
}
