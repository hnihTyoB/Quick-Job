package vn.thinher.quickjob.service.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.thinher.quickjob.domain.RestResponse;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = IdInvalidException.class) 
    public ResponseEntity<RestResponse<Object>> handleIdInvalidException(IdInvalidException idInvalidException) { 
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        restResponse.setError(idInvalidException.getMessage());
        restResponse.setMessage("Invalid ID");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
    } 
}
