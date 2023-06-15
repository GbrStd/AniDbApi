package com.example.anidbapi.repository;

import com.example.anidbapi.model.Genre;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GenreRepository extends MongoRepository<Genre, String> {

    Optional<Genre> findFirstByNameIgnoreCase(String name);

}
