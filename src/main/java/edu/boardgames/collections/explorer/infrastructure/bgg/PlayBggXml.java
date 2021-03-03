package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.boardgames.collections.explorer.domain.Play;
import edu.boardgames.collections.explorer.domain.Player;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.w3c.dom.Node;

public class PlayBggXml extends XmlNode implements Play {
	private final String id;

	public PlayBggXml(Node node) {
		super(node);
		this.id = id();
	}

	@Override
	public String id() {
		return string("@id");
	}

	@Override
	public LocalDate date() {
		return LocalDate.parse(string("@date"));
	}

	@Override
	public int quantity() {
		return number("@quantity").intValue();
	}

	@Override
	public int length() {
		return number("@length").intValue();
	}

	@Override
	public boolean incomplete() {
		return toBoolean("@incomplete");
	}

	@Override
	public String location() {
		return string("@location");
	}

	@Override
	public String boardGameId() {
		return string("item/@objectid");
	}

	@Override
	public String comments() {
		return string("comments");
	}

	@Override
	public List<Player> players() {
		return nodes("players/player").map(PlayerBggXml::new).collect(Collectors.toList());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PlayBggXml that = (PlayBggXml) o;
		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
