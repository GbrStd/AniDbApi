package com.example.anidbapi.dto;

import com.example.anidbapi.model.Episode;
import com.example.anidbapi.model.Genre;
import com.example.anidbapi.translate.TranslateField;
import lombok.Data;
import lombok.Value;

import java.util.List;

@Data
@Value
public class AnimeDto {

    String name;

    String imageUrl;

    @TranslateField
    String summary;

    String crunchyrollLink;

    @TranslateField
    String season;

    String year;

    List<Genre> genres;

    List<Episode> episodes;

}
