package guru.qa.rococo.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.model.kafka.Event;
import guru.qa.rococo.model.kafka.EventType;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.utils.MapWithWait;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class KafkaService implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaService.class);
  private static final Config CFG = Config.getInstance();
  private static final AtomicBoolean isRun = new AtomicBoolean(false);
  private static final Properties properties = new Properties();
  private static final ObjectMapper om = new ObjectMapper();
  private static final MapWithWait<String, UserJson> usersStore = new MapWithWait<>();
  private static final Queue<Event> eventsStore = new ConcurrentLinkedQueue<>();

  private final List<String> topics;
  private final Consumer<String, String> consumer;

  static {
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, CFG.kafkaAddress());
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
  }

  public KafkaService() {
    this(CFG.kafkaTopcis());
  }

  public KafkaService(List<String> topics) {
    this.topics = topics;
    this.consumer = new KafkaConsumer<>(properties);
  }

  public static UserJson getUser(String username) throws InterruptedException {
    return usersStore.get(username, 5000L);
  }

  public static List<Event> getEvents(){
    return new ArrayList<>(eventsStore);
  }

  public static Event getLastEvent() {
    return eventsStore.poll();
  }

  @Override
  public void run() {
    try {
      isRun.set(true);
      consumer.subscribe(topics);
      while (isRun.get()) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));
        for (ConsumerRecord<String,String> record : records) {
          String value = record.value();
          if("users".equals(record.topic())) {
            UserJson userJson = om.readValue(value, UserJson.class);
            usersStore.put(userJson.username(), userJson);
          }
          if ("events".equals(record.topic())) {
            System.out.println("ЛОГ СОБЫТИЯ " + value);
            try {
              Event event = om.readValue(value, Event.class);
              eventsStore.add(event);
            } catch (Exception e) {
              LOG.error("Failed to parse event: {}", value, e);
            }
          }
        }
      }
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    } finally {
      consumer.close();
      Thread.currentThread().interrupt();
    }
  }

  public void shutdown() {
    isRun.set(false);
  }
}
