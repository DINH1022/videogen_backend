package com.suplerteam.video_creator.exception;


import com.suplerteam.video_creator.response.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExHandler {
    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> resourceNotFoundExceptionHandler(
            ResourceNotFoundException exception){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new CustomErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(value = DuplicateResourceException.class)
    public ResponseEntity<CustomErrorResponse> DuplicateResourceExceptionHandler(
            DuplicateResourceException exception){
        return new ResponseEntity<>(
                new CustomErrorResponse(exception.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = CustomBadRequestException.class)
    public ResponseEntity<CustomErrorResponse> CustomBadRequestExceptionHandler(
            CustomBadRequestException exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CustomErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<CustomErrorResponse> handleGenericExceptionHandler(Exception exception){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomErrorResponse("An unexpected exception:"+exception.getMessage()));
    }

}
