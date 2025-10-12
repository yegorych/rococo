package guru.qa.rococo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String authHeader = request.getHeader("Authorization");
        String errorMessage;

        if (authHeader == null || authHeader.isBlank()) {
            errorMessage = "Требуется авторизация. Пожалуйста, выполните вход.";
        } else {
            Throwable cause = authException.getCause();
            if (cause instanceof JwtException && cause.getMessage() != null && cause.getMessage().contains("expired")) {
                errorMessage = "Сессия истекла. Пожалуйста, войдите заново.";
            } else {
                errorMessage = "Недействительный токен доступа.";
            }
        }
        Map<String, Object> body = Map.of("errors", List.of(errorMessage));
        response.getWriter().write(mapper.writeValueAsString(body));
    }
}

