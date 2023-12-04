package account.exceptions;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestControllerAdvice

public class ControllerExceptionHandler {



    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ValidationException.class, ConstraintViolationException.class})
    ResponseEntity<?> exceptionHandler(Exception e, ServletWebRequest request) {


        String requestURI = request.getRequest().getRequestURI();


        CustomErrorMessage body = new CustomErrorMessage(HttpStatus.BAD_REQUEST.value(), LocalDateTime.now(), "Bad Request", e.getMessage().replaceAll(".*: ", ""), requestURI);


        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ResponseStatusException.class})
    ResponseEntity<?> exceptionHandler(ResponseStatusException e, ServletWebRequest request) {


        System.out.println("ExceptionHandler");

        String requestURI = request.getRequest().getRequestURI();


        CustomErrorMessage body = new CustomErrorMessage(e.getBody().getStatus(), LocalDateTime.now(), HttpStatus.valueOf(e.getStatusCode().value()).getReasonPhrase(), e.getReason(), requestURI);


        return new ResponseEntity<>(body, HttpStatus.valueOf(body.getStatus()));
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({JdbcSQLIntegrityConstraintViolationException.class})
    ResponseEntity<?> exceptionHandler2(Exception e, ServletWebRequest request) {


        String requestURI = request.getRequest().getRequestURI();


        CustomErrorMessage body = new CustomErrorMessage(HttpStatus.BAD_REQUEST.value(), LocalDateTime.now(), "Bad Request", "Cannot allocate money to the same employee twice in the same period", requestURI);


        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


}