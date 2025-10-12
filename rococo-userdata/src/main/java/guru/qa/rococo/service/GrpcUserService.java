package guru.qa.rococo.service;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.userdata.RococoUserdataServiceGrpc;
import guru.qa.grpc.rococo.userdata.UserInfo;
import guru.qa.grpc.rococo.userdata.UserRequest;
import guru.qa.rococo.data.repository.UserRepository;
import guru.qa.rococo.ex.UserNotFoundException;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@GrpcService
public class GrpcUserService extends RococoUserdataServiceGrpc.RococoUserdataServiceImplBase {
    private final static Logger log = LoggerFactory.getLogger(GrpcUserService.class);

    private final UserRepository userRepository;

    public GrpcUserService(UserRepository userRepository) {
        this.userRepository =  userRepository;
    }

    @Override
    public void getUser(UserRequest request, StreamObserver<UserInfo> responseObserver) {
        final UserInfo user = userRepository.findByUsername(request.getUsername())
                .map(ue -> UserInfo.newBuilder()
                        .setId(ue.getId().toString())
                        .setUsername(ue.getUsername())
                        .setFirstname(ue.getFirstname() != null ? ue.getFirstname() : "")
                        .setLastname(ue.getLastname() != null ? ue.getLastname() : "")
                        .setAvatar(ue.getPhoto() != null ? ByteString.copyFrom(ue.getPhoto()) : ByteString.empty())
                        .build()
                )
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(
                                "Пользователь с именем \"%s\" не найден",
                                request.getUsername())
                        )
                );
        responseObserver.onNext(user);
        responseObserver.onCompleted();
    }

    @Transactional
    @Override
    public void updateUser(UserInfo request, StreamObserver<UserInfo> responseObserver) {
        userRepository.findByUsername(request.getUsername())
                .ifPresentOrElse(
                        ue -> {
                            ue.setFirstname(request.getFirstname());
                            ue.setLastname(request.getLastname());
                            ue.setPhoto(request.getAvatar().toByteArray());
                            userRepository.save(ue);
                        },
                        ()-> {
                            throw new UserNotFoundException(
                                String.format(
                                        "Пользователь с именем \"%s\" не найден",
                                        request.getUsername())
                            );
                        }
                );

        final UserInfo userInfo = UserInfo.newBuilder()
                .setId(request.getId())
                .setUsername(request.getUsername())
                .setFirstname(request.getFirstname())
                .setLastname(request.getLastname())
                .setAvatar(request.getAvatar())
                .build();

        responseObserver.onNext(userInfo);
        responseObserver.onCompleted();
    }

}


