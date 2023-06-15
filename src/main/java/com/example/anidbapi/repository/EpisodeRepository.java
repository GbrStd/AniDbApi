package com.example.anidbapi.repository;

import com.example.anidbapi.model.Episode;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EpisodeRepository extends MongoRepository<Episode, String> {
}
