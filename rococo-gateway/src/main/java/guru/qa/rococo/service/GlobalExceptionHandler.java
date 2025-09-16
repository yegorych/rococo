package guru.qa.rococo.service;


import guru.qa.rococo.ex.AccessDeniedException;
import guru.qa.rococo.service.utils.GrpcStatusMapper;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @Override
  protected @Nonnull ResponseEntity<Object> handleMethodArgumentNotValid(@Nonnull MethodArgumentNotValidException ex,
                                                                         @Nonnull HttpHeaders headers,
                                                                         @Nonnull HttpStatusCode status,
                                                                         @Nonnull WebRequest request) {
    LOG.error("==== ERROR ==== {}", ex.getMessage(), ex);
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors()
            .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                    "errors",
                    ex.getBindingResult()
                            .getFieldErrors()
                            .stream()
                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                            .toList()
                    )
            );
  }


  @ExceptionHandler(StatusRuntimeException.class)
  public ResponseEntity<Map<String, List<String>>> handleGrpcException(StatusRuntimeException ex) {
    LOG.error("==== ERROR ==== {}", ex.getMessage(), ex);
    return ResponseEntity
            .status(GrpcStatusMapper.map(ex.getStatus()))
            .body(Map.of("errors", List.of(ex.getMessage())));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, List<String>>> handleAccessDenied(AccessDeniedException ex) {
    LOG.error("==== ERROR ==== {}", ex.getMessage(), ex);
    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(Map.of("errors", List.of(ex.getMessage())));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, List<String>>> handleException(Exception ex) {
    LOG.error("==== ERROR ==== {}", ex.getMessage(), ex);
    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("errors", List.of("Что-то пошло не так")));
  }

}