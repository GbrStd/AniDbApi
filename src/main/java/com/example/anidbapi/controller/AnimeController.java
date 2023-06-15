package com.example.anidbapi.controller;

import com.example.anidbapi.constant.ApiMapping;
import com.example.anidbapi.core.GenericController;
import com.example.anidbapi.dto.AnimeDto;
import com.example.anidbapi.model.PageCache;
import com.example.anidbapi.response.MessageResponse;
import com.example.anidbapi.service.AnimeService;
import com.example.anidbapi.service.PageCacheService;
import com.example.anidbapi.translate.Language;
import com.example.anidbapi.translate.TranslateException;
import com.example.anidbapi.translate.Translator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(ApiMapping.ANIME)
public class AnimeController extends GenericController {

    private final PageCacheService pageCacheService;
    private final AnimeService animeService;
    private final Translator translator;

    public AnimeController(PageCacheService pageCacheService, AnimeService animeService, Translator translator) {
        this.pageCacheService = pageCacheService;
        this.animeService = animeService;
        this.translator = translator;
    }

    @GetMapping
    public ResponseEntity<MessageResponse> searchAnimes(@RequestParam(value = "s") String search,
                                                        @RequestParam(value = "page", required = false, defaultValue = "0") int page) {
        final PageCache searched = pageCacheService.searchAnimes(search, page);
        final Map<String, Object> content = new HashMap<>();
        content.put("page", searched);
        if (searched.isHasNextPage()) {
            content.put("nextPage", ApiMapping.ANIME + "?s=" + search + "&page=" + (page + 1));
        }
        return response(content, HttpStatus.OK);
    }

    @GetMapping("/{name}")
    public ResponseEntity<MessageResponse> exactAnime(@PathVariable String name,
                                                      @RequestParam(value = "lang", required = false) Language lang) throws TranslateException {
        AnimeDto animeDto = animeService.findAnimeByName(name);

        if (lang != null) {
            animeDto = translator.translateObject(
                    animeDto,
                    Language.ENGLISH,
                    lang
            );
        }

        return response(Map.of("anime", animeDto), HttpStatus.OK);
    }

    @GetMapping("/popular")
    public ResponseEntity<MessageResponse> popularAnimes() {
        final PageCache popularAnimes = pageCacheService.popularAnimes();
        return response(Map.of("page", popularAnimes), HttpStatus.OK);
    }

}
