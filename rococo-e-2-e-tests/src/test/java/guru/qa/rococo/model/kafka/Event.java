package guru.qa.rococo.model.kafka;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record Event(
        String username,
        String paintingId,
        EventType eventType,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
        Date eventTime) {
}
