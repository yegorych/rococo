package guru.qa.rococo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Map;

public class TokenDecoder {
    public static Map<String, Object> decodeBearerToken(String bearerToken) {
        try {
            String token = bearerToken.replace("Bearer ", "").trim();
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT format");
            }

            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(payload, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode token", e);
        }
    }
}
