
import com.transwarp.generator.kafka.kafka.KafkaSendClient;

import java.util.concurrent.ExecutionException;

public class test {
  public static void main(String[] args) {
    KafkaSendClient kafkaSendClient = new KafkaSendClient("172.18.120.32:9092,172.18.120.32:9092,172.18.120.33:9092," +
            ",172.18.120.34:9092");

    try {
      kafkaSendClient.listTopics();
    } catch (ExecutionException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
