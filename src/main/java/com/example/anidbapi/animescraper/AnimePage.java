package com.example.anidbapi.animescraper;

import lombok.Data;

import java.util.List;

@Data
public class AnimePage {
    private long id;
    private String name;
    private String type;
    private String imageUrl;
    private String crunchyrollLink;
    private String season;
    private List<String> genres;
    private String summary;
    private String year;
    private List<EpisodeInfo> episodes;
    private AnimePage sequel;
    private AnimePage prequel;
    private Double rating;
}