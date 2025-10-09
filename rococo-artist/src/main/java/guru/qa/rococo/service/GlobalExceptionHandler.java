package guru.qa.rococo.service;


import guru.qa.rococo.ex.ArtistNotFoundException;
import guru.qa.rococo.ex.InvalidUUIDException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcAdvice
public class GlobalExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @GrpcExceptionHandler(ArtistNotFoundException.class)
    public StatusRuntimeException handleArtistNotFound(ArtistNotFoundException e) {
        return Status.NOT_FOUND.withDescription(e.getMessage()).withCause(e).asRuntimeException();
    }

    @GrpcExceptionHandler(InvalidUUIDException.class)
    public StatusRuntimeException handleInvalidUUIDException(InvalidUUIDException e) {
        return Status.INVALID_ARGUMENT.withDescription(e.getMessage()).withCause(e).asRuntimeException();
    }

    @GrpcExceptionHandler(Exception.class)
    public StatusRuntimeException handleException(Exception e) {
        log.error("Unhandled exception caught in gRPC advice", e);
        return Status.INTERNAL.withDescription("Ошибка при получении художников").withCause(e).asRuntimeException();
    }

}
