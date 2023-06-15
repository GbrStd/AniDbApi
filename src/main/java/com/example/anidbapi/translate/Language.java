package com.example.anidbapi.translate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Language {

    ENGLISH("en"),
    ARABIC("ar"),
    CHINESE("zh"),
    FRENCH("fr"),
    GERMAN("de"),
    HINDI("hi"),
    INDONESIAN("id"),
    IRISH("ga"),
    ITALIAN("it"),
    JAPANESE("ja"),
    KOREAN("ko"),
    POLISH("pl"),
    PORTUGUESE("pt"),
    RUSSIAN("ru"),
    SPANISH("es"),
    TURKISH("tr"),
    VIETNAMESE("vi");

    private final String code;

}
