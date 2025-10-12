package guru.qa.rococo.api.logging;

import okhttp3.logging.HttpLoggingInterceptor;
import java.util.regex.Pattern;

public class CompactImageHttpLogger implements HttpLoggingInterceptor.Logger {

    private static final Pattern[] sensitiveFields = new Pattern[]{
            Pattern.compile("\"photo\":\"([^\"]{20})[^\"]+\""),
            Pattern.compile("\"image\":\"([^\"]{20})[^\"]+\""),
            Pattern.compile("\"avatar\":\"([^\"]{20})[^\"]+\""),
            Pattern.compile("\"content\":\"([^\"]{20})[^\"]+\"")
    };

    @Override
    public void log(String message) {
        String sanitized = message;
        for (Pattern pattern : sensitiveFields) {
            sanitized = pattern.matcher(sanitized).replaceAll("\"$1...\"");
        }
        System.out.println(sanitized);
    }
}
