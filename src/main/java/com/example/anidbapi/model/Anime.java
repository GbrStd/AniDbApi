package com.example.anidbapi.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@Data
public class Anime {

    @Id
    private String id;

    private String name;

    private long aniId;

    private String imageUrl;

    private String summary;

    private String crunchyrollLink;

    private String season;

    private String year;

    private List<Genre> genreList = new ArrayList<>();

    private List<Episode> episodeList = new ArrayList<>();

    private Double rating;

}
