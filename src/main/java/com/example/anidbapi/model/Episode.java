package com.example.anidbapi.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalDateTime;

@Document
@Data
public class Episode {

    @Id
    private String id;

    private Integer episodeNumber;

    private String name;

    private String videoUrl;

    private LocalDateTime airDate;

    private Duration duration;

}
