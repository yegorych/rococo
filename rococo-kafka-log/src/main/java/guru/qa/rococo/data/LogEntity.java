package guru.qa.rococo.data;

import guru.qa.rococo.model.Event;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;


@Getter
@Setter
@Entity
@Table(name = "log")
public class LogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
  private UUID id;

  @Column(name = "username")
  private String username;

  @Column(name = "painting_id", columnDefinition = "BINARY(16)")
  private UUID paintingId;

  @Column(name = "event_type")
  private String eventType;

  @Column(name = "event_time")
  @Temporal(TemporalType.TIMESTAMP)
  private Date eventTime;

  @CreationTimestamp
  @Column(name = "created_at")
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;

  public static LogEntity fromJson(Event event) {
    LogEntity entity = new LogEntity();
    entity.setUsername(event.username());
    entity.setPaintingId(UUID.fromString(event.paintingId()));
    entity.setEventType(event.eventType().name());
    entity.setEventTime(event.eventTime());
    return entity;
  }
}
