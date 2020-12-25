package com.transwarp.generator.kafka.kafka;


import com.transwarp.generator.kafka.context.KafkaContext;
import com.transwarp.generator.kafka.partitioners.KafkaHashPartitioner;
import com.transwarp.generator.kafka.utils.KafkaUtils;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.KafkaFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class KafkaSendClient {

  private static Logger LOG = LoggerFactory.getLogger(KafkaSendClient.class);
  private Properties kafkaProperties = new Properties();
  private AdminClient client;
  private int timeout = 6000;

  public void connection(String bootstrapServerAddress) {
    kafkaProperties = KafkaUtils.getNoSecuredAdminClientProperties(bootstrapServerAddress);
    client = KafkaAdminClient.create(kafkaProperties);
    LOG.info("Successfully connect to Kafka: " + bootstrapServerAddress);
  }

  public KafkaSendClient(Properties properties) {
    this.kafkaProperties = properties;
  }

  public KafkaSendClient(KafkaContext kafkaContext) {
    kafkaProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContext.getBootstrapServerAddress());
    kafkaProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
    kafkaProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
    kafkaProperties.put(ProducerConfig.ACKS_CONFIG, kafkaContext.getAck());
    kafkaProperties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, KafkaHashPartitioner.class);
    kafkaProperties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaContext.getCompressionCodec());
    kafkaProperties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, kafkaContext.getMaxInFlightRequestsPerConnection());
    //以下还未自定义配置
    //batch.size默认值就是16kb
    kafkaProperties.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaContext.getBatchSize());
    //linger.ms默认是0
    kafkaProperties.put(ProducerConfig.LINGER_MS_CONFIG, kafkaContext.getLingerMs());
    //缓冲区的大小
    kafkaProperties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, kafkaContext.getBufferSize());
  }

  public KafkaProducer getProducer() {
    KafkaProducer kafkaProducer = new KafkaProducer(kafkaProperties);
    return kafkaProducer;
  }

  public boolean createTopic(String targetTopic, int partitionNum, short refactorNum) throws Exception {
    try {
      NewTopic newTopic = new NewTopic(targetTopic, partitionNum, refactorNum);
      List<NewTopic> topics = new ArrayList<>();
      topics.add(newTopic);
      CreateTopicsOptions options = new CreateTopicsOptions();
      options.timeoutMs(timeout);
      CreateTopicsResult result = client.createTopics(topics, options);
      for (Map.Entry<String, KafkaFuture<Void>> e : result.values().entrySet()) {
        KafkaFuture<Void> future = e.getValue();
        future.get();
        boolean success = !future.isCompletedExceptionally();
        if (success) {
          LOG.info("Successfully create Kafka topic " + targetTopic + " ,partition " + partitionNum + " ,refactor " + refactorNum);
        }
        return success;
      }
      return false;
    } catch (Exception e) {
      LOG.warn("Topic Already exists." + e.getMessage());
      return true;
    }
  }

  public boolean createTopicIfNotExists(String name, int numPartitions, short replicationFactor) throws Exception {
    if (!listTopics().contains(name)) {
      return createTopic(name, numPartitions, replicationFactor);
    }
    LOG.info("Kafka topic " + name + " already exists ");
    return true;
  }

  public Set<String> listTopics() throws ExecutionException, InterruptedException {
    ListTopicsOptions options = new ListTopicsOptions();
    //设置超时时间
    options.timeoutMs(timeout);
    //不列出kafka内部topic
    options.listInternal(false);
    ListTopicsResult result = client.listTopics(options);
    Set<String> topics = result.names().get();
    System.out.println("List kafka topics: " + topics.toString());
    LOG.info("List kafka topics: " + topics.toString());
    return topics;
  }

}
