package com.example.anidbapi.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageResponse {

    private final ZonedDateTime timestamp;
    private final int status;
    @JsonIgnore
    private Map<Object, Object> map = new HashMap<>();

    public static MessageResponseBuilder builder() {
        return new MessageResponseBuilder();
    }

    @JsonAnySetter
    public void add(Object key, Object value) {
        map.put(key, value);
    }

    @JsonAnyGetter
    public Map<?, ?> getMap() {
        return map;
    }

    public static class MessageResponseBuilder {

        private Map<Object, Object> map = new HashMap<>();

        private ZonedDateTime timestamp;

        private int status;

        MessageResponseBuilder() {
            timestamp = ZonedDateTime.now();
        }

        public MessageResponseBuilder message(Map<Object, Object> map) {
            this.map = map;
            return this;
        }

        public MessageResponseBuilder add(Object key, Object value) {
            this.map.put(key, value);
            return this;
        }

        public MessageResponseBuilder genericMessage(Object message) {
            this.map.put("message", message);
            return this;
        }

        public MessageResponseBuilder timestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public MessageResponseBuilder status(int status) {
            this.status = status;
            return this;
        }

        public MessageResponse build() {
            MessageResponse messageResponse = new MessageResponse(this.timestamp, this.status);
            this.map.forEach(messageResponse::add);
            return messageResponse;
        }

    }

}
