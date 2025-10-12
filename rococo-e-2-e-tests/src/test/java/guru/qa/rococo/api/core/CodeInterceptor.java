package guru.qa.rococo.api.core;

import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class CodeInterceptor implements Interceptor {
  @NotNull
  @Override
  public Response intercept(Chain chain) throws IOException {
    final Response response = chain.proceed(chain.request());
    if (response.isRedirect()) {
      String location = Objects.requireNonNull(
          response.header("Location")
      );
      if (location.contains("code=")) {
        ApiLoginExtension.setCode(
            StringUtils.substringAfter(
                location, "code="
            )
        );
      }
    }
    return response;
  }
}
