package com.example.anidbapi.handler;

import com.example.anidbapi.response.MessageResponse;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<MessageResponse> handleConversionFailedException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MessageResponse.builder()
                .genericMessage(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build());
    }

}
