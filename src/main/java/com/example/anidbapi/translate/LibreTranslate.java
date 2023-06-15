package com.example.anidbapi.translate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class LibreTranslate extends Translator {

    private static final String URL = "https://translate.terraprint.co";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String translate(String text, @NonNull Language from, @NonNull Language to) throws TranslateException {
        final String url = URL + "/translate";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString("q=" + text + "&source=" + from.getCode() + "&target=" + to.getCode()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        final HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new TranslateException("Cant connect to libretranslate", e);
        }

        if (response.statusCode() != 200)
            throw new TranslateException("Status code is not 200: " + response.statusCode());

        try {
            Map<?, ?> map = OBJECT_MAPPER.readValue(response.body(), Map.class);
            if (map.containsKey("translatedText"))
                return map.get("translatedText").toString();
        } catch (JsonProcessingException e) {
            throw new TranslateException("Cant parse json", e);
        }

        throw new TranslateException("Could not translate text: " + response.body());
    }

}
