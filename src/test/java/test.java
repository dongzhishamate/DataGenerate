
import com.transwarp.generator.kafka.kafka.KafkaSendClient;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.concurrent.ExecutionException;

public class test {
  public static void main(String[] args) {
    KafkaSendClient kafkaSendClient = new KafkaSendClient("172.18.120.31:9092");

    try {
      kafkaSendClient.listTopics();
    } catch (ExecutionException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
