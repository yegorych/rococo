package guru.qa.rococo.controller;


import guru.qa.rococo.config.RococoGatewayServiceConfig;
import guru.qa.rococo.ex.AccessDeniedException;
import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.service.aggregator.UserdataAggregatorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@SecurityRequirement(name = RococoGatewayServiceConfig.OPEN_API_AUTH_SCHEME)
public class UserController {

  private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

  private final UserdataAggregatorService userDataClient;

  @Autowired
  public UserController(UserdataAggregatorService userDataClient) {
    this.userDataClient = userDataClient;
  }

  @GetMapping("/user")
  public UserJson currentUser(@AuthenticationPrincipal Jwt principal) {
    String username = principal.getClaim("sub");
    return userDataClient.getUser(username);
  }

  @PatchMapping("/user")
  public UserJson updateUserInfo(@AuthenticationPrincipal Jwt principal,
                                 @Valid @RequestBody UserJson user) {
    String username = principal.getClaim("sub");
    if (!username.equals(user.username())) {
      throw new AccessDeniedException("Вы не можете изменить данные другого пользователя");
    }
    return userDataClient.updateUser(user);
  }
}
