package edu.boardgames.collections.explorer.infrastructure.bgg;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlInput;
import edu.boardgames.collections.explorer.infrastructure.xml.XmlNode;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class GeekBuddyBgg implements GeekBuddy {
	private final String username;
	private final String name;

	GeekBuddyBgg(String username, String name) {
		this.username = Objects.requireNonNull(username);
		this.name = Objects.requireNonNull(name);
	}

	@Override
	public String username() {
		return username;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public List<BoardGame> ownedCollection() {
		return this.fromInputStream(new CollectionRequest(username).owned().withStats().withoutExpansions().asInputStream());
	}

	@Override
	public List<BoardGame> wantToPlayCollection() {
		return fromInputStream(new CollectionRequest(username).wantToPlay().withStats().withoutExpansions().asInputStream());
	}

	private static List<BoardGame> fromInputStream(InputStream inputStream) {
		return XmlNode.nodes(new XmlInput().read(inputStream), "//item")
				.map(CollectionBoardGameBggXml::new)
				.collect(Collectors.toList());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GeekBuddyBgg that = (GeekBuddyBgg) o;
		return username.equals(that.username);
	}

	@Override
	public int hashCode() {
		return Objects.hash(username);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", GeekBuddyBgg.class.getSimpleName() + "[", "]")
				.add(String.format("username='%s'", username))
				.add(String.format("name='%s'", name))
				.toString();
	}
}
