package guru.qa.rococo.model;

import java.util.Date;

public record Event(String username, String paintingId, EventType eventType, Date eventTime) {
}
