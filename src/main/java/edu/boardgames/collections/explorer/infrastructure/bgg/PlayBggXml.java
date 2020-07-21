package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.Play;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.apache.commons.lang3.BooleanUtils;
import org.w3c.dom.Node;

import java.time.LocalDate;
import java.util.Objects;

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
	public boolean incomplete() {
		return BooleanUtils.toBoolean(number("@incomplete").intValue());
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
