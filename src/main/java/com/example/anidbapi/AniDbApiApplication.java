package com.example.anidbapi;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class AniDbApiApplication {

    static {
        Properties properties = new Properties();
        try {
            properties.load(AniDbApiApplication.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            System.err.println("Failed to load application.properties");
            System.exit(1);
        }
        final boolean useProxy = Boolean.parseBoolean(properties.get("anidbapi.animescraper.request-via-proxy").toString());
        if (useProxy) {
            System.setProperty("jdk.http.auth.proxying.disabledSchemes", "");
            System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(AniDbApiApplication.class, args);
    }

    @Bean
    public Queue searchAnimeQueue(@Value("${queue.searchanime.name}") String name) {
        return new Queue(name, true);
    }

}
