package com.example.anidbapi.service;

import com.example.anidbapi.animescraper.AnimeScraper;
import com.example.anidbapi.animescraper.AnimeScraperException;
import com.example.anidbapi.animescraper.FailedToScrapeException;
import com.example.anidbapi.animescraper.SearchPage;
import com.example.anidbapi.core.CacheableMongoService;
import com.example.anidbapi.exception.AnimeNotFoundException;
import com.example.anidbapi.model.PageCache;
import com.example.anidbapi.rabbit.SearchAnimeProducer;
import com.example.anidbapi.repository.PageCacheRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ForkJoinPool;

@Service
public class PageCacheService extends CacheableMongoService<PageCache, String> {

    private final PageCacheRepository pageCacheRepository;
    private final InfoAnimeCacheService infoAnimeCacheService;
    private final AnimeScraper animeScraper;
    private final SearchAnimeProducer searchAnimeProducer;

    @Value("${anidbapi.search.enable-recursive-search}")
    private boolean enableRecursiveSearch;

    @Value("${anidbapi.search.max-recursion-search-depth}")
    private int maxRecursionSearchDepth;

    public PageCacheService(MongoRepository<PageCache, String> repository, PageCacheRepository pageCacheRepository, InfoAnimeCacheService infoAnimeCacheService, AnimeScraper animeScraper, SearchAnimeProducer searchAnimeProducer) {
        super(repository);
        this.pageCacheRepository = pageCacheRepository;
        this.infoAnimeCacheService = infoAnimeCacheService;
        this.animeScraper = animeScraper;
        this.searchAnimeProducer = searchAnimeProducer;
    }

    private PageCache cacheSearchAnimePage(String animeName, int pageIndex) {

        if (findFirstBySearchQueryIgnoreCaseAndPageIndex(animeName, pageIndex) != null) {
            return findFirstBySearchQueryIgnoreCaseAndPageIndex(animeName, pageIndex);
        }

        final SearchPage page;
        try {
            page = animeScraper.searchAnimes(animeName, pageIndex);
        } catch (AnimeScraperException e) {
            return save(new PageCache(animeName, pageIndex, false));
            //throw new AnimeNotFoundException("Page %s not found");
        } catch (FailedToScrapeException e) {
            throw new RuntimeException(e);
        }

        final PageCache pageCache = new PageCache(
                animeName,
                pageIndex,
                page.isHasNextPage()
        );

        pageCache.setInfoAnimeCache(infoAnimeCacheService.cacheAnimes(page.getPageResults()));

        return save(pageCache);
    }

    public PageCache findFirstBySearchQueryIgnoreCaseAndPageIndex(String searchQuery, int pageIndex) {
        return pageCacheRepository.findFirstBySearchQueryIgnoreCaseAndPageIndex(searchQuery, pageIndex).orElse(null);
    }

    public PageCache searchAnimes(String animeName, int pageIndex) throws AnimeNotFoundException {

        PageCache page = cacheSearchAnimePage(animeName, pageIndex);

        if (enableRecursiveSearch && page.isHasNextPage()) {

            // Already cache the next pages if it exists
            try (ForkJoinPool forkJoinPool = ForkJoinPool.commonPool()) {

                forkJoinPool.execute(() -> {
                    for (int currentPage = 0; currentPage < maxRecursionSearchDepth; currentPage++) {
                        try {

                            int index = (currentPage + 1) + pageIndex;
                            if (findFirstBySearchQueryIgnoreCaseAndPageIndex(animeName, index) != null) {
                                continue;
                            }

                            final SearchPage search;
                            try {
                                search = animeScraper.searchAnimes(animeName, index);
                            } catch (FailedToScrapeException | AnimeScraperException e) {
                                continue;
                            }

                            final PageCache pageCache = new PageCache(
                                    animeName,
                                    index,
                                    search.isHasNextPage()
                            );

                            pageCache.setInfoAnimeCache(infoAnimeCacheService.cacheAnimes(search.getPageResults()));

                            searchAnimeProducer.send(pageCache);

                            if (!search.isHasNextPage()) {
                                break;
                            }

                        } catch (AnimeNotFoundException e) {
                            break;
                        }
                    }
                });

            }
        }

        return page;
    }

    public PageCache popularAnimes() throws AnimeNotFoundException {

        final LocalDateTime now = LocalDateTime.now();

        final String pageName = "popular-" + now.getMonth() + '-' + now.getYear();

        if (findFirstBySearchQueryIgnoreCaseAndPageIndex(pageName, 0) != null) {
            return findFirstBySearchQueryIgnoreCaseAndPageIndex(pageName, 0);
        }

        final SearchPage page;
        try {
            page = animeScraper.popularAnimes();
        } catch (AnimeScraperException e) {
            throw new AnimeNotFoundException("Page %s not found");
        } catch (FailedToScrapeException e) {
            throw new RuntimeException(e);
        }

        final PageCache pageCache = new PageCache(
                pageName,
                0,
                page.isHasNextPage()
        );

        pageCache.setInfoAnimeCache(infoAnimeCacheService.cacheAnimes(page.getPageResults()));

        return save(pageCache);
    }
}
