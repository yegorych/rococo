package guru.qa.rococo.service;


import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.userdata.UserInfo;
import guru.qa.grpc.rococo.userdata.UserRequest;
import guru.qa.rococo.data.UserEntity;
import guru.qa.rococo.data.repository.UserRepository;
import guru.qa.rococo.ex.UserNotFoundException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrpcUserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private StreamObserver<UserInfo> responseObserver;

  @Captor
  private ArgumentCaptor<UserInfo> userInfoCaptor;

  private GrpcUserService grpcUserService;

  private UserEntity testUser;

  @BeforeEach
  void setUp() {
    grpcUserService = new GrpcUserService(userRepository);
    testUser = new UserEntity();
    testUser.setId(UUID.randomUUID());
    testUser.setUsername("testUser");
    testUser.setFirstname("John");
    testUser.setLastname("Doe");
    testUser.setPhoto("avatar".getBytes());
  }

  // --- GET USER TESTS ---

  @Test
  void shouldReturnUserInfo_WhenUserExists() {
    when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

    UserRequest request = UserRequest.newBuilder().setUsername("testUser").build();

    grpcUserService.getUser(request, responseObserver);

    verify(responseObserver).onNext(userInfoCaptor.capture());
    verify(responseObserver).onCompleted();

    UserInfo captured = userInfoCaptor.getValue();
    assertEquals(testUser.getId().toString(), captured.getId());
    assertEquals("testUser", captured.getUsername());
    assertEquals("John", captured.getFirstname());
    assertEquals("Doe", captured.getLastname());
    assertEquals(ByteString.copyFrom("avatar".getBytes()), captured.getAvatar());
  }

  @Test
  void shouldThrowException_WhenUserNotFound() {
    when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
    UserRequest request = UserRequest.newBuilder().setUsername("ghost").build();

    UserNotFoundException ex = assertThrows(UserNotFoundException.class,
            () -> grpcUserService.getUser(request, responseObserver));

    assertTrue(ex.getMessage().contains("Пользователь с именем"));
    verify(responseObserver, never()).onNext(any());
    verify(responseObserver, never()).onCompleted();
  }

  // --- UPDATE USER TESTS ---

  @Test
  void shouldUpdateUser_WhenUserExists() {
    when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

    UserInfo request = UserInfo.newBuilder()
            .setId(testUser.getId().toString())
            .setUsername("testUser")
            .setFirstname("Updated")
            .setLastname("User")
            .setAvatar(ByteString.copyFrom("new_avatar".getBytes()))
            .build();

    grpcUserService.updateUser(request, responseObserver);

    verify(userRepository).save(testUser);
    verify(responseObserver).onNext(userInfoCaptor.capture());
    verify(responseObserver).onCompleted();

    UserInfo updated = userInfoCaptor.getValue();
    assertEquals("Updated", updated.getFirstname());
    assertEquals("User", updated.getLastname());
    assertEquals(ByteString.copyFrom("new_avatar".getBytes()), updated.getAvatar());
  }

  @Test
  void shouldThrowException_WhenUpdatingNonExistingUser() {
    when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

    UserInfo request = UserInfo.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setUsername("unknown")
            .build();

    UserNotFoundException ex = assertThrows(UserNotFoundException.class,
            () -> grpcUserService.updateUser(request, responseObserver));

    assertTrue(ex.getMessage().contains("Пользователь с именем"));
    verify(userRepository, never()).save(any());
    verify(responseObserver, never()).onNext(any());
    verify(responseObserver, never()).onCompleted();
  }

  @Test
  void shouldHandleNullFieldsGracefully() {
    testUser.setFirstname(null);
    testUser.setLastname(null);
    testUser.setPhoto(null);
    when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

    UserRequest request = UserRequest.newBuilder().setUsername("testUser").build();

    grpcUserService.getUser(request, responseObserver);

    verify(responseObserver).onNext(userInfoCaptor.capture());
    UserInfo info = userInfoCaptor.getValue();

    assertEquals("", info.getFirstname());
    assertEquals("", info.getLastname());
    assertTrue(info.getAvatar().isEmpty());
  }

  @Test
  void shouldSaveUserWithUpdatedPhoto_WhenPhotoIsEmptyInRequest() {
    when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

    UserInfo request = UserInfo.newBuilder()
            .setId(testUser.getId().toString())
            .setUsername("testUser")
            .setFirstname("NoPhoto")
            .setLastname("User")
            .build(); // no avatar set

    grpcUserService.updateUser(request, responseObserver);

    verify(userRepository).save(testUser);
    verify(responseObserver).onNext(userInfoCaptor.capture());
    verify(responseObserver).onCompleted();

    assertEquals("NoPhoto", testUser.getFirstname());
    assertTrue(request.getAvatar().isEmpty());
  }
}
