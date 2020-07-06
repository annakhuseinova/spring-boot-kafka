package com.annakhuseinova.apachekafka.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handle(MethodArgumentNotValidException e){
        List<FieldError> errorList = e.getBindingResult().getFieldErrors();
        String errorMessage = errorList.stream().map(fieldError -> fieldError.getField() + " - "
                + fieldError.getDefaultMessage()).sorted().collect(Collectors.joining(", "));
        log.info("errorMessage : {}", errorMessage);
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
