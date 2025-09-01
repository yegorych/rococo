package guru.qa.rococo.controller.v2;

import guru.qa.rococo.service.grpc.GrpcUserdataClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


//@RestController
//@RequestMapping("/api/v2/users")
//@SecurityRequirement(name = RococoGatewayServiceConfig.OPEN_API_AUTH_SCHEME)
public class UserV2Controller {

  private static final Logger LOG = LoggerFactory.getLogger(UserV2Controller.class);

  private final GrpcUserdataClient userDataClient;

  @Autowired
  public UserV2Controller(GrpcUserdataClient userDataClient) {
    this.userDataClient = userDataClient;
  }


//  @GetMapping("/all")
//  public Page<UserJson> allUsers(@AuthenticationPrincipal Jwt principal,
//                                 @PageableDefault Pageable pageable,
//                                 @RequestParam(required = false) String searchQuery) {
//    String username = principal.getClaim("sub");
//    return userDataClient.allUsers(username, pageable, searchQuery);
//  }
}
