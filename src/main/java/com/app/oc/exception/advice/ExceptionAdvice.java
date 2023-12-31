package com.app.oc.exception.advice;

import com.app.oc.exception.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    // //400
     @ResponseStatus(HttpStatus.BAD_REQUEST) //400
     @ExceptionHandler(value =
     {IllegalArgumentException.class,IllegalStateException.class})
     public ErrorResult illegalExHandler(RuntimeException e) {
     return new ErrorResult(HttpStatus.BAD_REQUEST.value(), e.getMessage());
     }

     //서버오류
     @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
     @ExceptionHandler
     public ErrorResult exHandler(Exception e) {
     return new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), "내부오류");
     }

    // Bean
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        body.put("errors", errors);
        //
        log.info("Errors", errors);
        return new ResponseEntity<>(body, headers, status);
    }

}
