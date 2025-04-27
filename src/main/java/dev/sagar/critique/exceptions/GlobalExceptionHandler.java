package dev.sagar.critique.exceptions;

import dev.sagar.critique.domain.dtos.ErrorDto;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDto handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    log.error("Caught MethodArgumentNotValidException", ex);

    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));

    return ErrorDto.builder().status(HttpStatus.BAD_REQUEST.value()).message(message).build();
  }

  @ExceptionHandler(StorageException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDto handleStorageException(StorageException ex) {
    log.error("Caught StorageException", ex);

    return ErrorDto.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message("Unable to save or retrieve resources at this time")
        .build();
  }

  @ExceptionHandler(BaseException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDto handleBaseException(BaseException ex) {
    log.error("Caught BaseException", ex);

    return ErrorDto.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message("An unexpected error occurred")
        .build();
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDto handleException(Exception ex) {
    log.error("Caught unexpected Exception", ex);

    return ErrorDto.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message("An unexpected error occurred")
        .build();
  }
}
