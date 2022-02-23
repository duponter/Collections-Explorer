package edu.boardgames.collections.explorer.infrastructure.xml;

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

import org.apache.commons.lang3.BooleanUtils;

import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import org.w3c.dom.Node;

public final class XmlHttpRequest {
	private static final System.Logger LOGGER = System.getLogger(XmlHttpRequest.class.getName());

	private final Supplier<HttpClient> httpClientSupplier;
	private final UrlFactory urlFactory;
	private final Map<String, String> options = new HashMap<>();

	public XmlHttpRequest(UrlFactory urlFactory) {
		this(urlFactory, () -> HttpClient.newBuilder().build());
	}

    public XmlHttpRequest(UrlFactory urlFactory, Supplier<HttpClient> httpClientSupplier) {
		this.httpClientSupplier = httpClientSupplier;
		this.urlFactory = urlFactory;
	}

    public XmlHttpRequest copy() {
		XmlHttpRequest request = new XmlHttpRequest(this.urlFactory, this.httpClientSupplier);
		request.options.putAll(this.options);
		return request;
	}

    public XmlHttpRequest addOption(String name, String value) {
		this.options.put(name, URLEncoder.encode(value, Charset.defaultCharset()));
		return this;
	}

    public XmlHttpRequest enableOption(String name) {
        return this.addOption(name, Objects.toString(BooleanUtils.toInteger(true)));
    }

    public Node asNode() {
        return new XmlInput().read(this.send(BodyHandlers.ofInputStream()));
    }

    public String asXml() {
        return this.send(BodyHandlers.ofLines()).collect(Collectors.joining());
    }

    private <T> T send(BodyHandler<T> bodyHandler) {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(this.urlFactory.create(buildQueryString()))
				.version(Version.HTTP_2)
				.GET()
				.build();

		RetryPolicy<HttpResponse<T>> retryPolicy = RetryPolicy.<HttpResponse<T>>builder()
				.abortIf(response -> response.statusCode() == 200)
				.handleResultIf(response -> response.statusCode() == 202)
				.withBackoff(1L, 100L, ChronoUnit.SECONDS)
				.withMaxRetries(5)
				.onFailedAttempt(e -> LOGGER.log(Level.WARNING, String.format("Failed to get a response from %s", request.uri())))
				.onRetry(e -> LOGGER.log(Level.WARNING, String.format("Retrying to get a response from %s", request.uri())))
				.onSuccess(e -> LOGGER.log(Level.INFO, String.format("Got response from %s", request.uri())))
				.onFailure(e -> LOGGER.log(Level.INFO, String.format("Failed to get response from %s", request.uri())))
				.build();

		return Failsafe.with(retryPolicy).get(() -> httpClientSupplier.get().send(request, bodyHandler)).body();
	}

    private String buildQueryString() {
        return this.options.entrySet().stream()
                .map(Object::toString)
                .collect(Collectors.joining("&"));
    }
}
