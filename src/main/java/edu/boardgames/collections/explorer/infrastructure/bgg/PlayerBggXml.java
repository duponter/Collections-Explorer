package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.Objects;

import edu.boardgames.collections.explorer.domain.Player;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;
import org.w3c.dom.Node;

public class PlayerBggXml extends XmlNode implements Player {
	private final String name;

	public PlayerBggXml(Node node) {
		super(node);
		this.name = name();
	}

	@Override
	public String username() {
		return string("@username");
	}

	@Override
	public String name() {
		return string("@name");
	}

	@Override
	public boolean newPlayer() {
		return toBoolean("@new");
	}

	@Override
	public boolean won() {
		return toBoolean("@won");
	}

	@Override
	public double score() {
		return number("@score").doubleValue();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PlayerBggXml that = (PlayerBggXml) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
