import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.management.ManagementFactory;

public class test {
  public static void main(String[] args) {

//    KafkaSendClient kafkaSendClient = new KafkaSendClient("172.18.120.32:9092,172.18.120.32:9092,172.18.120.33:9092," +
//            ",172.18.120.34:9092");
//
//    try {
//      kafkaSendClient.listTopics();
//    } catch (ExecutionException e) {
//      e.printStackTrace();
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
    System.out.println(ManagementFactory.getThreadMXBean().getTotalStartedThreadCount());
    System.out.println(Runtime.getRuntime().availableProcessors());

  }
}
