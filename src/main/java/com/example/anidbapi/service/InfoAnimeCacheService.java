package com.example.anidbapi.service;

import com.example.anidbapi.animescraper.PageResult;
import com.example.anidbapi.core.CacheableMongoService;
import com.example.anidbapi.model.InfoAnimeCache;
import com.example.anidbapi.repository.InfoAnimeCacheRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InfoAnimeCacheService extends CacheableMongoService<InfoAnimeCache, String> {

    private final InfoAnimeCacheRepository infoAnimeCacheRepository;

    public InfoAnimeCacheService(MongoRepository<InfoAnimeCache, String> repository, InfoAnimeCacheRepository infoAnimeCacheRepository) {
        super(repository);
        this.infoAnimeCacheRepository = infoAnimeCacheRepository;
    }

    public InfoAnimeCache findById(String id) {
        return infoAnimeCacheRepository.findById(id).orElse(null);
    }

    // TODO: Implement cache search (maybe?)
    public List<InfoAnimeCache> cacheAnimes(PageResult[] results) {
        List<InfoAnimeCache> infoAnimeCaches = new ArrayList<>(results.length);

        for (PageResult result : results) {
            InfoAnimeCache infoAnimeCache = new InfoAnimeCache(
                    result.getId(),
                    result.getName(),
                    result.getImageUrl(),
                    result.getRating(),
                    result.getType(),
                    result.getNumEps(),
                    result.getAiredDate(),
                    result.getEndedDate()
            );
            infoAnimeCaches.add(infoAnimeCache);
        }

        return saveAll(infoAnimeCaches);
    }
}
