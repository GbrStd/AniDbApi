package com.example.anidbapi.handler;

import com.example.anidbapi.response.MessageResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.ZonedDateTime;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<MessageResponse> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        int statusCode = Integer.parseInt(status.toString());

        if (message.isBlank())
            message = HttpStatus.valueOf(statusCode).getReasonPhrase();

        return ResponseEntity.status(statusCode).body(MessageResponse.builder()
                .genericMessage(message)
                .timestamp(ZonedDateTime.now())
                .status(statusCode)
                .build());
    }

}
