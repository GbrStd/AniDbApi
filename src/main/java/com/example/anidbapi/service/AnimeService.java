package com.example.anidbapi.service;

import com.example.anidbapi.animescraper.*;
import com.example.anidbapi.dto.AnimeDto;
import com.example.anidbapi.exception.AnimeNotFoundException;
import com.example.anidbapi.model.Anime;
import com.example.anidbapi.model.Episode;
import com.example.anidbapi.model.Genre;
import com.example.anidbapi.repository.AnimeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnimeService {

    private final AnimeRepository animeRepository;
    private final AnimeScraper animeScraper;
    private final GenreService genreService;
    private final EpisodeService episodeService;

    public AnimeService(AnimeRepository animeRepository, AnimeScraper animeScraper, GenreService genreService, EpisodeService episodeService) {
        this.animeRepository = animeRepository;
        this.animeScraper = animeScraper;
        this.genreService = genreService;
        this.episodeService = episodeService;
    }

    private Anime save(Anime anime) {
        return animeRepository.save(anime);
    }

    public AnimeDto findAnimeByName(String name) throws AnimeNotFoundException {

        final Optional<Anime> animeOptional = animeRepository.findFirstByNameIgnoreCase(name);

        if (animeOptional.isPresent()) {
            final Anime anime = animeOptional.get();
            return new AnimeDto(anime.getName(),
                    anime.getImageUrl(),
                    anime.getSummary(),
                    anime.getCrunchyrollLink(),
                    anime.getSeason(),
                    anime.getYear(),
                    anime.getGenreList(),
                    anime.getEpisodeList()
            );
        }

        final AnimePage animePage;
        try {
            animePage = animeScraper.findExactAnime(name);
        } catch (AnimeScraperException e) {
            throw new AnimeNotFoundException("Anime %s not found".formatted(name));
        } catch (FailedToScrapeException e) {
            throw new RuntimeException(e);
        }

        final Anime temp = new Anime();

        temp.setName(animePage.getName());
        temp.setAniId(animePage.getId());
        temp.setImageUrl(animePage.getImageUrl());
        temp.setSummary(animePage.getSummary());
        temp.setCrunchyrollLink(animePage.getCrunchyrollLink());
        temp.setSeason(animePage.getSeason());
        temp.setYear(animePage.getYear());
        temp.setRating(animePage.getRating());

        List<Genre> genreList = new ArrayList<>();
        for (String genre : animePage.getGenres()) {
            genreList.add(genreService.findGenreByName(genre));
        }

        List<Episode> episodeList = new ArrayList<>();
        for (EpisodeInfo episodeInfo : animePage.getEpisodes()) {
            Episode episode = new Episode();
            episode.setEpisodeNumber(episodeInfo.getEpisodeNumber());
            episode.setName(episodeInfo.getName());
            episode.setVideoUrl(episodeInfo.getVideoUrl());
            episode.setAirDate(episodeInfo.getAirDate());
            episode.setDuration(episodeInfo.getDuration());
            episodeList.add(episode);
        }

        temp.setGenreList(genreList);
        temp.setEpisodeList(episodeList);
        //temp.setEpisodeList(episodeService.saveAll(episodeList));

        final Anime anime = save(temp);
        return new AnimeDto(anime.getName(),
                anime.getImageUrl(),
                anime.getSummary(),
                anime.getCrunchyrollLink(),
                anime.getSeason(),
                anime.getYear(),
                anime.getGenreList(),
                anime.getEpisodeList()
        );
    }

}
