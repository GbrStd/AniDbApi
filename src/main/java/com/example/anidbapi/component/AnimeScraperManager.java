package com.example.anidbapi.component;

import com.example.anidbapi.animescraper.AniDbScraper;
import com.example.anidbapi.animescraper.AnimeScraper;
import com.example.anidbapi.proxy.ProxyFactory;
import com.example.anidbapi.proxy.ProxyType;
import com.example.anidbapi.proxy.ProxyWorkerPool;
import com.example.anidbapi.proxy.ProxyWorkerPoolImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
public class AnimeScraperManager {

    private static final String TYPE = "anidbapi.animescraper.type";

    @Value("${anidbapi.animescraper.request-via-proxy}")
    private boolean useProxy;

    @Bean
    @Order(Integer.MIN_VALUE) // this bean must be created first
    public ProxyWorkerPool getProxyWorkerPool() {
        if (useProxy)
            return new ProxyWorkerPoolImpl(List.of(ProxyFactory.createProxy("gw.thunderproxies.net:5959", ProxyType.SOCKS, "thunderlpymxmoe60B-res-ROW", "xeqyljggpwcr19T")));
        return null;
    }

    @Bean
    @ConditionalOnProperty(name = TYPE, havingValue = "anidb")
    public AnimeScraper getAniDbScraper() {
        return new AniDbScraper(getProxyWorkerPool());
    }

    @Bean
    @ConditionalOnProperty(name = TYPE, havingValue = "myanimelist")
    public AnimeScraper getMyAnimeListScraper() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
