package com.simplechatbotproxy.chat.controller;

import com.simplechatbotproxy.chat.model.ErrorMessage;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@Slf4j
@RestControllerAdvice("com.simplechatbotproxy.chat")
public class ChatControllerAdvice {
    @ExceptionHandler(value={IOException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage ioExceptionHandler(IOException e){
        log.error(e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler(value=IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage illegalStateHandler(NullPointerException e){
        log.error(e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage illegalArgumentHandler(IllegalArgumentException e){
        log.error(e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage statusRuntime(StatusRuntimeException e){
        log.error(e.getMessage());
        return new ErrorMessage(e.getMessage());
    }
}
