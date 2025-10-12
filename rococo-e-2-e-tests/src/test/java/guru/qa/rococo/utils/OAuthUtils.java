package guru.qa.rococo.utils;

import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@ParametersAreNonnullByDefault
public class OAuthUtils {

  private static final SecureRandom secureRandom = new SecureRandom();

  @Nonnull
  public static String generateCodeVerifier() {
    byte[] codeVerifier = new byte[32];
    secureRandom.nextBytes(codeVerifier);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
  }

  @SneakyThrows
  @Nonnull
  public static String generateCodeChallenge(String codeVerifier) {
    byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
    messageDigest.update(bytes, 0, bytes.length);
    byte[] digest = messageDigest.digest();
    return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
  }
}
