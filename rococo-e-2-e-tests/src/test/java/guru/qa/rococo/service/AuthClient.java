package guru.qa.rococo.service;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface AuthClient {
    void requestRegisterForm();
    void register(String username, String password);
    String preRequest();
    String login(String username, String password);
    String token(String code, String codeVerifier);
}
