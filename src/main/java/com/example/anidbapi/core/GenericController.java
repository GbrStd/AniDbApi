package com.example.anidbapi.core;

import com.example.anidbapi.response.MessageResponse;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;
import java.util.Map;

public abstract class GenericController {

    protected <T> ResponseEntity<MessageResponse> response(@NonNull T t, @NonNull HttpStatus statusCode) {
        return ResponseEntity.status(statusCode).body(MessageResponse.builder()
                .genericMessage(t)
                .timestamp(ZonedDateTime.now())
                .status(statusCode.value())
                .build());
    }

    @SuppressWarnings("unchecked")
    protected ResponseEntity<MessageResponse> response(@NonNull Map<Object, Object> map, @NonNull HttpStatus statusCode) {
        return ResponseEntity.status(statusCode).body(MessageResponse.builder()
                .message(map)
                .timestamp(ZonedDateTime.now())
                .status(statusCode.value())
                .build());
    }

}
