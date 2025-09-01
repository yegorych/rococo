package guru.qa.rococo.service;


import guru.qa.rococo.AccessDeniedException;
import guru.qa.rococo.service.utils.GrpcStatusMapper;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);


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
            .status(500)
            .body(Map.of("errors", List.of("Что-то пошло не так")));
  }

}