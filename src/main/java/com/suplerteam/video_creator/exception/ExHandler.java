package com.suplerteam.video_creator.exception;


import com.suplerteam.video_creator.response.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExHandler {
    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> resourceNotFoundExceptionHandler(
            ResourceNotFoundException exception){
        System.out.println(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
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

    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleUserNotFoundExceptionHandler(
            UsernameNotFoundException exception){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new CustomErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(value = AuthenticationException.class)
    public ResponseEntity<CustomErrorResponse> handleAuthenticationException(AuthenticationException exception) {
        String message = exception.getMessage();
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new CustomErrorResponse("Authentication failed: " + message));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<CustomErrorResponse> handleGenericExceptionHandler(Exception exception){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomErrorResponse("An unexpected exception:"+exception.getMessage()));
    }

}
