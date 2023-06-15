package com.example.anidbapi.repository;

import com.example.anidbapi.model.PageCache;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PageCacheRepository extends MongoRepository<PageCache, String> {

    Optional<PageCache> findFirstBySearchQueryIgnoreCaseAndPageIndex(String searchQuery, int pageIndex);

}
