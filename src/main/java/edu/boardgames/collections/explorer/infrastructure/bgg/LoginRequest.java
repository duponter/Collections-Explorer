package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Objects;

import dev.failsafe.Failsafe;
import dev.failsafe.Fallback;
import dev.failsafe.event.ExecutionAttemptedEvent;
import dev.failsafe.function.CheckedConsumer;

/**
 http://tutorials.jenkov.com/java-cryptography/cipher.html
 https://stackoverflow.com/a/43779197/1571325
 https://stackoverflow.com/a/18228702/1571325
 */
class LoginRequest {
	private final String username;
	private final String password;

	LoginRequest(String username, String password) {
		this.username = Objects.requireNonNull(username);
		this.password = Objects.requireNonNull(password);
	}

	HttpClient send() {
		CookieManager cm = new CookieManager();
		HttpClient httpClient = HttpClient.newBuilder().cookieHandler(cm).followRedirects(Redirect.NORMAL).build();

		HttpRequest login = HttpRequest.newBuilder()
				.uri(URI.create("http://boardgamegeek.com/login"))
				.version(Version.HTTP_2)
				.POST(BodyPublishers.ofString(String.format("username=%s&password=%s", username, password)))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.build();

		CheckedConsumer<ExecutionAttemptedEvent<?>> checkedConsumer = failure -> {
			throw new IllegalArgumentException(String.format("Unable to login with username %s", username));
		};
		Failsafe.with(Fallback.of(checkedConsumer)).get(() -> httpClient.send(login, BodyHandlers.discarding()));
		return httpClient;
	}
}
