package com.example.anidbapi.rabbit;

import com.example.anidbapi.model.PageCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SearchAnimeProducer {

    private final RabbitTemplate rabbitTemplate;

    private final Queue queue;

    public SearchAnimeProducer(RabbitTemplate rabbitTemplate, Queue queue) {
        this.rabbitTemplate = rabbitTemplate;
        this.queue = queue;
    }

    public void send(PageCache pageCache) {
        log.info("Enviando mensagem");
        rabbitTemplate.convertAndSend(queue.getName(), pageCache);
    }

}
