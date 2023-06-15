package com.example.anidbapi.rabbit;

import com.example.anidbapi.model.PageCache;
import com.example.anidbapi.service.PageCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SearchAnimeConsumer {

    private final PageCacheService pageCacheService;

    public SearchAnimeConsumer(PageCacheService pageCacheService) {
        this.pageCacheService = pageCacheService;
    }

    @RabbitListener(queues = "${queue.searchanime.name}")
    public void receive(@Payload PageCache pageCache) {
        log.info("Consumindo mensagem");
        if (pageCacheService.findFirstBySearchQueryIgnoreCaseAndPageIndex(pageCache.getSearchQuery(), pageCache.getPageIndex()) != null) {
            return;
        }
        pageCacheService.save(pageCache);
    }

}
