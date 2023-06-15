package com.example.anidbapi.animescraper;

import com.example.anidbapi.proxy.Proxy;
import com.example.anidbapi.proxy.ProxyWorkerException;
import com.example.anidbapi.proxy.ProxyWorkerPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class AnimeScraper {

    private static final int REQUESTS_WITH_SAME_CLIENT = 5;
    private final ProxyWorkerPool proxyWorkerPool;
    private final AtomicInteger requestsDone = new AtomicInteger(0);
    private HttpClient httpClient = null;
    private Proxy currentProxy = null;

    protected AnimeScraper(@Nullable ProxyWorkerPool proxyWorkerPool) {
        this.proxyWorkerPool = proxyWorkerPool;
    }

    public abstract SearchPage searchAnimes(String animeName, int pageIndex) throws AnimeScraperException, FailedToScrapeException;

    public abstract AnimePage findExactAnime(String animeName) throws AnimeScraperException, FailedToScrapeException;

    public abstract SearchPage popularAnimes() throws AnimeScraperException, FailedToScrapeException;

    /**
     * Do a request to an Anime Database website
     *
     * @param page the page to request
     * @return the response
     */
    protected String doRequest(String page, boolean permitsRedirect) throws IOException, InterruptedException, AnimeScraperException {

        var httpClientBuilder = HttpClient.newBuilder();
        var requestBuilder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(page))
                .timeout(java.time.Duration.ofSeconds(5))
                .setHeader("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 OPR/99.0.0.0");

        final HttpRequest request;
        final HttpResponse<String> response;

        if (proxyWorkerPool != null) {
            final Proxy proxy;
            synchronized (this) {
                if (currentProxy == null || requestsDone.get() >= REQUESTS_WITH_SAME_CLIENT) {
                    requestsDone.set(0);
                    httpClient = null; // force to create a new http client
                    try {
                        proxy = proxyWorkerPool.getProxy();
                    } catch (ProxyWorkerException e) {
                        throw new IOException("Failed to get proxy", e);
                    }
                    log.info("Using proxy: " + proxy.getIp() + ":" + proxy.getPort());
                    currentProxy = proxy;
                } else {
                    proxy = currentProxy;
                }
                httpClientBuilder.proxy(ProxySelector.of(new InetSocketAddress(proxy.getIp(), proxy.getPort())));
                if (proxy.getPassword() != null) {
                    httpClientBuilder.authenticator(new Authenticator() {
                        @Override
                        public PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(proxy.getUsername(), proxy.getPassword().toCharArray());
                        }
                    });
                    String encoded = new String(Base64.getEncoder().encode((proxy.getUsername() + ":" + proxy.getPassword()).getBytes()));
                    requestBuilder.setHeader("Proxy-Authorization", "Basic " + encoded);
                }
                request = requestBuilder.build();
                if (httpClient == null)
                    httpClient = httpClientBuilder.build();
            }
            try {
                response = proxyWorkerPool.executeUntilSuccess(proxy, p -> httpClient.send(request, HttpResponse.BodyHandlers.ofString()));
            } catch (ProxyWorkerException e) {
                throw new IOException("Failed to execute request", e);
            }
        } else {
            synchronized (this) {
                request = requestBuilder.build();
                if (httpClient == null)
                    httpClient = httpClientBuilder.build();
            }
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }

        requestsDone.incrementAndGet();

        if (response.statusCode() != 200) {
            if (!permitsRedirect || response.statusCode() != 302) {
                throw new AnimeScraperException("Failed to get anime, status code: " + response.statusCode());
            }
        }

        return response.body();
    }

}
