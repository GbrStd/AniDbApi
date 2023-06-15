package com.example.anidbapi.config;

import com.example.anidbapi.translate.Language;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;

public class StringToLanguageConverter implements Converter<String, Language> {

    @Override
    public Language convert(String source) {
        return Arrays.stream(Language.values())
                .filter(lang -> lang.getCode().equalsIgnoreCase(source))
                .findFirst()
                .orElseThrow();
    }

}
