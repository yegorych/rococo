package guru.qa.rococo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rococo.model.rest.ErrorJson;
import okhttp3.ResponseBody;

import java.io.IOException;

public class ErrorMessageResolver {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String getErrorMessage(ResponseBody body) {
        try {
            return MAPPER.readValue(body.string(), ErrorJson.class).error()[0];
        } catch (IOException e) {
            throw new RuntimeException("Error parsing error", e);
        }
    }
}
