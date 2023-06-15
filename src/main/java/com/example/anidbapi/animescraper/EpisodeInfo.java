package com.example.anidbapi.animescraper;

import lombok.Data;
import lombok.Value;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Value
public class EpisodeInfo {
    Integer episodeNumber;
    String name;
    String videoUrl;
    LocalDateTime airDate;
    Duration duration;
}
