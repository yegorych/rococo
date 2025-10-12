package guru.qa.rococo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

  private static final String LOGIN_VIEW_NAME = "login";
  private static final String PRE_REQ_ATTR = "SPRING_SECURITY_SAVED_REQUEST";
  private static final String PRE_REQ_URI = "/oauth2/authorize";

  private final String rococoFrontUri;

  public LoginController(@Value("${rococo-front.base-uri}") String rococoFrontUri) {
    this.rococoFrontUri = rococoFrontUri;
  }

  @GetMapping("/login")
  public String login(HttpSession session) {
    DefaultSavedRequest savedRequest = (DefaultSavedRequest) session.getAttribute(PRE_REQ_ATTR);
    if (savedRequest == null || !savedRequest.getRequestURI().equals(PRE_REQ_URI)) {
      return "redirect:" + rococoFrontUri;
    }
    return LOGIN_VIEW_NAME;
  }
}
