package guru.qa.rococo.service;


import guru.qa.rococo.ex.MuseumNotFoundException;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcAdvice
public class GlobalExceptionHandler{

    private final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @GrpcExceptionHandler(MuseumNotFoundException.class)
    public StatusRuntimeException handlePaintingNotFound(MuseumNotFoundException e) {
        return Status.NOT_FOUND.withDescription(e.getMessage()).withCause(e).asRuntimeException();
    }


    @GrpcExceptionHandler(Exception.class)
    public StatusRuntimeException handleException(Exception e) {
        log.error("Unhandled exception caught in gRPC advice", e);
        throw Status.INTERNAL.withDescription("Ошибка при получении музеев").withCause(e).asRuntimeException();
    }

}
