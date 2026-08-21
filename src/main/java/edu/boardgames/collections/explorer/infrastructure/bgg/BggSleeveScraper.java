package edu.boardgames.collections.explorer.infrastructure.bgg;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a type of card sleeve information.
 */
record SleeveInfo(String nameOrType, String dimensions, String count) {
}

/**
 * Scrapes card sleeve information from BoardGameGeek.
 *
 * Note: Web scraping is dependent on the website's HTML structure, which can change.
 * The CSS selectors used here might need adjustment if BoardGameGeek updates its page layout.
 */
public class BggSleeveScraper {

    public static void main(String[] args) {
        // BoardGameGeek ID and name for "Unconscious Mind"
        String gameId = "329500";
        String gameName = "unconscious-mind";
        // Construct the URL for the sleeves subpage
        String url = "https://boardgamegeek.com/boardgame/" + gameId + "/" + gameName + "/sleeves";

        System.out.println("Attempting to scrape sleeve information from: " + url);
        System.out.println("----------------------------------------------------");

        List<SleeveInfo> allSleeveInfo = new ArrayList<>();

        try {
            // Connect to the website and get the HTML document.
            // It's good practice to set a User-Agent to identify your bot.
            // A timeout is also important to prevent the program from hanging indefinitely.
            Document doc = Jsoup.connect(url)
                .timeout(15000) // 15 seconds timeout
                .get();

            // --- CSS Selector Strategy ---
            // The selectors are crucial and are most likely to need updates if the BGG page structure changes.
            // From inspecting the BGG sleeves page (as of early 2024/2025), sleeve information often appears
            // in list items (<li>) within an unordered list (<ul>) that might have a specific class or be
            // within a known container. Sometimes it's in tables.

            // Primary target: Look for list items within a common content area.
            // The class 'geekitem_section_content' seems to hold the main content for these subpages.
            // Within that, 'ul' elements often contain lists of details.
            // We are looking for list items that directly contain the sleeve details.
            // Example selector: div.geekitem_section_content ul > li
            // This selector targets list items (li) that are direct children of a ul,
            // which itself is inside a div with class 'geekitem_section_content'.

            Elements potentialSleeveEntries = doc.select("div.geekitem_section_content ul > li");

            if (potentialSleeveEntries.isEmpty()) {
                // Fallback: Sometimes the structure might be a table directly.
                // This selector would target rows in a table, assuming a common BGG table class.
                // This is less likely for the /sleeves page based on current BGG structure but good as a fallback.
                // Elements tableRows = doc.select("table.forum_table tr, table.geekitem_infotable tr");
                // For now, we'll focus on the list item structure which is more common for this type of page.
                System.out.println("No list items found with the primary selector. Trying to find simple list items.");
                potentialSleeveEntries = doc.select("li"); // A very broad selector as a last resort
            }


            boolean foundData = false;
            for (Element entry : potentialSleeveEntries) {
                String text = entry.text(); // Get all text within the list item

                // Heuristic to identify sleeve information:
                // - Must contain "mm" (for dimensions)
                // - Must contain a number followed by "card" or "cards"
                // - Often starts with a number (for count) or a size description.
                // Example text: "110 cards: 45mm x 68mm" or "Standard Card Game (63.5mm x 88mm) - 200 cards"

                if (text.matches(".*\\d+.*[cC]ards?.*") && text.contains("mm")) {
                    // Attempt to parse out details. This is a simple parser and might need refinement.
                    String countStr = "";
                    String dimStr = "";
                    String nameStr = text; // Default name to the full text, then refine

                    // Try to extract count (e.g., "110 cards")
                    java.util.regex.Matcher countMatcher = java.util.regex.Pattern.compile("(\\d+)\\s*[cC]ards?").matcher(text);
                    if (countMatcher.find()) {
                        countStr = countMatcher.group(1) + " cards";
                        // Remove count from nameStr for cleaner name
                        nameStr = nameStr.replace(countMatcher.group(0), "").trim();
                    }

                    // Try to extract dimensions (e.g., "45mm x 68mm")
                    java.util.regex.Matcher dimMatcher = java.util.regex.Pattern.compile("(\\d+(\\.\\d+)?mm\\s*[xX×]\\s*\\d+(\\.\\d+)?mm)").matcher(text);
                    if (dimMatcher.find()) {
                        dimStr = dimMatcher.group(1);
                        // Remove dimensions from nameStr for cleaner name
                        nameStr = nameStr.replace(dimStr, "").trim();
                    } else {
                        // Simpler dimension match if the above fails (e.g. only one dimension listed or different format)
                        dimMatcher = java.util.regex.Pattern.compile("(\\d+(\\.\\d+)?mm)").matcher(text);
                        if (dimMatcher.find()) {
                            dimStr = dimMatcher.group(1);
                            nameStr = nameStr.replace(dimStr, "").trim();
                        }
                    }

                    // Clean up the name string (remove colons, leading/trailing hyphens)
                    nameStr = nameStr.replaceAll("[:\\-]", "").trim();
                    if (nameStr.endsWith(",")) {
                        nameStr = nameStr.substring(0, nameStr.length() -1).trim();
                    }


                    if (!dimStr.isEmpty() && !countStr.isEmpty()) {
                        allSleeveInfo.add(new SleeveInfo(nameStr, dimStr, countStr));
                        foundData = true;
                    }
                }
            }

            if (foundData) {
                System.out.println("\nSuccessfully extracted sleeve information:");
                for (SleeveInfo info : allSleeveInfo) {
                    System.out.println(info);
                }
            } else {
                System.out.println("Could not automatically identify sleeve information with the current selectors and heuristics.");
                System.out.println("Please inspect the page HTML (" + url + ") and adjust the CSS selectors or parsing logic in the code.");
                System.out.println("\nTips for finding selectors/patterns:");
                System.out.println("1. Go to the URL in your browser.");
                System.out.println("2. Right-click on the data you want to extract (e.g., '110 cards: 45mm x 68mm').");
                System.out.println("3. Choose 'Inspect' or 'Inspect Element'.");
                System.out.println("4. In the developer tools, find the HTML element (e.g., `<li>`, `<td>`) that contains this text.");
                System.out.println("5. Note any unique `id` or `class` attributes of this element or its parents.");
                System.out.println("6. Update the `doc.select(...)` line with a more specific CSS selector.");
                System.out.println("7. Examine the text format to refine the regular expressions for parsing details.");
            }

        } catch (IOException e) {
            System.err.println("Error connecting to or parsing the URL: " + url);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred:");
            e.printStackTrace();
        }
    }
}
