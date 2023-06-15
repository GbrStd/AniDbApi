package com.example.anidbapi.repository;

import com.example.anidbapi.model.InfoAnimeCache;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InfoAnimeCacheRepository extends MongoRepository<InfoAnimeCache, String> {

}
