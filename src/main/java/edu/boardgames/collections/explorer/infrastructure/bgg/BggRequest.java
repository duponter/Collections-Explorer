package edu.boardgames.collections.explorer.infrastructure.bgg;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.apache.commons.lang3.BooleanUtils;

import java.io.InputStream;
import java.lang.System.Logger.Level;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract class BggRequest<R extends BggRequest<R>> {
	private static final System.Logger LOGGER = System.getLogger(BggRequest.class.getName());

	private final Supplier<HttpClient> httpClientSupplier;
	private final BggUrlFactory urlFactory;
	private final Map<String, String> options = new HashMap<>();

	BggRequest(BggUrlFactory urlFactory) {
		this(urlFactory, () -> HttpClient.newBuilder().build());
	}

	BggRequest(BggUrlFactory urlFactory, Supplier<HttpClient> httpClientSupplier) {
		this.httpClientSupplier = httpClientSupplier;
		this.urlFactory = urlFactory;
	}

	abstract R self();

	R addOption(String name, String value) {
		this.options.put(name, URLEncoder.encode(value, Charset.defaultCharset()));
		return this.self();
	}

	R enableOption(String name) {
		return this.addSwitchableOption(name, true);
	}

	R disableOption(String name) {
		return this.addSwitchableOption(name, false);
	}

	private R addSwitchableOption(String name, boolean value) {
		return this.addOption(name, Objects.toString(BooleanUtils.toInteger(value)));
	}

	private String buildQueryString() {
		return this.options.entrySet().stream()
				.map(Object::toString)
				.collect(Collectors.joining("&"));
	}

	public InputStream asInputStream() {
		return this.send(BodyHandlers.ofInputStream());
	}

	public Stream<String> asLines() {
		return this.send(BodyHandlers.ofLines());
	}

	private <T> T send(BodyHandler<T> bodyHandler) {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(this.urlFactory.create(buildQueryString()))
				.version(Version.HTTP_2)
				.GET()
				.build();

		RetryPolicy<HttpResponse<T>> retryPolicy = new RetryPolicy<HttpResponse<T>>()
				.abortIf(response -> response.statusCode() == 200)
				.handleResultIf(response -> response.statusCode() == 202)
				.withBackoff(1L, 100L, ChronoUnit.SECONDS)
				.withMaxRetries(5)
				.onFailedAttempt(e -> LOGGER.log(Level.WARNING, String.format("Failed to get a response from %s", request.uri())))
				.onRetry(e -> LOGGER.log(Level.WARNING, String.format("Retrying to get a response from %s", request.uri())))
				.onSuccess(e -> LOGGER.log(Level.INFO, String.format("Got response from %s", request.uri())))
				.onFailure(e -> LOGGER.log(Level.INFO, String.format("Failed to get response from %s", request.uri())));

		return Failsafe.with(retryPolicy).get(() -> httpClientSupplier.get().send(request, bodyHandler)).body();
	}
}
