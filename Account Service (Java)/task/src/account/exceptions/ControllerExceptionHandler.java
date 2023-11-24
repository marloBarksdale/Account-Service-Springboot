package account.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Set;

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


        String requestURI = request.getRequest().getRequestURI();


        CustomErrorMessage body = new CustomErrorMessage(e.getBody().getStatus(), LocalDateTime.now(), "Bad Request", e.getReason(), requestURI);


        return new ResponseEntity<>(body, HttpStatus.valueOf(body.getStatus()));
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({JdbcSQLIntegrityConstraintViolationException.class})
    ResponseEntity<?> exceptionHandler2(Exception e, ServletWebRequest request) {


        String requestURI = request.getRequest().getRequestURI();


        CustomErrorMessage body = new CustomErrorMessage(HttpStatus.BAD_REQUEST.value(), LocalDateTime.now(), "Bad Request", "Cannot allocate money to the same employee twice in the same period", requestURI);


        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity handle(ConstraintViolationException constraintViolationException) {
        Set<ConstraintViolation<?>> violations = constraintViolationException.getConstraintViolations();
        String errorMessage = "";
        if (!violations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            violations.forEach(violation -> builder.append(" " + violation.getMessage()));
            errorMessage = builder.toString();
        } else {
            errorMessage = "ConstraintViolationException occured.";
        }
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }


}