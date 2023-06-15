package com.example.anidbapi.service;

import com.example.anidbapi.model.Genre;
import com.example.anidbapi.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Genre findGenreByName(String name) {
        final Optional<Genre> optionalGenre = genreRepository.findFirstByNameIgnoreCase(name);
        if (optionalGenre.isPresent())
            return optionalGenre.get();
        final Genre genre = new Genre();
        genre.setName(name);
        return genreRepository.save(genre);
    }

}
