package com.example.anidbapi.component;

import com.example.anidbapi.translate.LibreTranslate;
import com.example.anidbapi.translate.Translator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class TranslationManager {

    @Bean
    public Translator getTranslator() {
        return new LibreTranslate();
    }

}
