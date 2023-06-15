package com.example.anidbapi.service;

import com.example.anidbapi.model.Episode;
import com.example.anidbapi.repository.EpisodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EpisodeService {

    private final EpisodeRepository episodeRepository;

    public EpisodeService(EpisodeRepository episodeRepository) {
        this.episodeRepository = episodeRepository;
    }

    public Episode save(Episode episode) {
        return episodeRepository.save(episode);
    }

    public List<Episode> saveAll(List<Episode> episodes) {
        return episodeRepository.saveAll(episodes);
    }

}
