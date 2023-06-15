package com.example.anidbapi.repository;

import com.example.anidbapi.model.Anime;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AnimeRepository extends MongoRepository<Anime, String> {

    Optional<Anime> findFirstByNameIgnoreCase(String name);

}
