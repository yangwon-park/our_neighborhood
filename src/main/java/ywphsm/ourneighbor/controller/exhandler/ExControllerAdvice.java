package ywphsm.ourneighbor.controller.exhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> userExHandle(IllegalArgumentException e) {
        log.error("[exceptionHandle] ex", e);
        return new ResponseEntity<>("사용자 입력오류", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<String> exHandle(Exception e) {
        log.error("[exceptionHandle] ex", e);
        return new ResponseEntity<>("서버 오류", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
