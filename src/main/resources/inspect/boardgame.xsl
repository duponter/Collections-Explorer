<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <html>
            <head>
                <title>My Boardgame Collection</title>
                <link rel="stylesheet" type="text/css" href="http://yegor256.github.io/tacit/tacit.min.css"/>
            </head>
            <body>
                <h2>My Boardgame Collection</h2>
                <table border="1">
                    <tr>
                        <th>Name</th>
                        <th>Players</th>
                        <th>Playtime</th>
                    </tr>
                    <xsl:for-each select="items/item">
                        <tr>
                            <td><xsl:value-of select="name/@value" /> (<xsl:value-of select="yearpublished/@value" />)</td>
                            <td><xsl:value-of select="minplayers/@value" />-<xsl:value-of select="maxplayers/@value" /></td>
                            <td><xsl:value-of select="minplaytime/@value" />-<xsl:value-of select="maxplaytime/@value" /> mins</td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
