package guru.qa.rococo.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Base64;

public class ImgBase64Utils {
    private static final Base64.Encoder encoder = Base64.getEncoder();

    public static String imageToBase64(String path){
        try {
            return "data:image/png;base64," + encoder.encodeToString(new ClassPathResource(path).getInputStream().readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
