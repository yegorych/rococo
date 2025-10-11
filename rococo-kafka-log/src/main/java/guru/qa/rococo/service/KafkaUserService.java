package guru.qa.rococo.service;

import guru.qa.rococo.data.LogEntity;
import guru.qa.rococo.data.repository.LogRepository;
import guru.qa.rococo.model.Event;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static guru.qa.rococo.model.EventType.CREATE;

@Component
public class KafkaUserService {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaUserService.class);

  private final LogRepository logRepository;

  @Autowired
  public KafkaUserService(LogRepository logRepository) {
    this.logRepository = logRepository;
  }

  @Transactional
  @KafkaListener(topics = "events", groupId = "logs")
  public void listener(@Payload Event event, ConsumerRecord<String, Event> cr) {
      LOG.info("### Kafka consumer record: {}", cr.toString());
      LogEntity logEntity = LogEntity.fromJson(event);
      logRepository.save(logEntity);
      String eventType = CREATE.equals(event.eventType())
              ? "created"
              : "updated";
      LOG.info(
              "### User '{}' successfully {} painting with id: {}",
              event.username(),
              eventType,
              event.paintingId()
      );
  }

}
