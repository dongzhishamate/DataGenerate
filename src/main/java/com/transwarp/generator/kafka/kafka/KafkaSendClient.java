package com.transwarp.generator.kafka.kafka;


import com.transwarp.generator.kafka.utils.KafkaUtils;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.KafkaFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class KafkaSendClient {

  private static Logger LOG = LoggerFactory.getLogger(KafkaSendClient.class);
  private Properties kafkaProperties;
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

  public KafkaSendClient(String bootstrapServerAddress) {
    kafkaProperties = KafkaUtils.getNoSecuredAdminClientProperties(bootstrapServerAddress);
    client = KafkaAdminClient.create(kafkaProperties);
    LOG.info("Successfully connect to Kafka: " + bootstrapServerAddress);
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
