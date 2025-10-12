package guru.qa.rococo.service.utils;

import io.grpc.Status;
import org.springframework.http.HttpStatus;

public class GrpcStatusMapper {

    private GrpcStatusMapper() {}

    public static HttpStatus map(Status status) {
        return switch (status.getCode()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
            case ALREADY_EXISTS -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
