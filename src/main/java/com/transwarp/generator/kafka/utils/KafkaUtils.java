package com.transwarp.generator.kafka.utils;

import com.transwarp.generator.kafka.partitioners.KafkaHashPartitioner;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.Set;


public class KafkaUtils {
  private static Logger LOG = LoggerFactory.getLogger(KafkaUtils.class);
  private static final String DEFAULT_KEY_DESERIALIZER = StringDeserializer.class.getName();
  private static final String DEFAULT_VALUE_DESERIALIZER = StringDeserializer.class.getName();
  private static final String DEFAULT_KEY_SERIALIZER = StringSerializer.class.getName();
  private static final String DEFAULT_VALUE_SERIALIZER = StringSerializer.class.getName();

  private static final String HASH_PARTITIONER_CLASS = KafkaHashPartitioner.class.getName();

  public static final String BOOTSTRAP_SERVERS = "bootstrap.servers";
  public static final String CLIENT_ID = "client.id";

  //producer config
  public static final String PRODUCER_ACKS = "acks";
  public static final String PRODUCER_BUFFER_MEMORY = "buffer.memory";
  public static final String PRODUCER_COMPRESSION_TYPE = "compression.type";
  public static final String PRODUCER_RETRIES = "retries";
  public static final String PRODUCER_BATCH_SIZE = "batch.size";
  public static final String PRODUCER_LINGER_MS = "linger.ms";
  public static final String PRODUCER_MAX_BLOCK_MS = "max.block.ms";
  public static final String PRODUCER_MAX_REQUEST_SIZE = "max.request.size";
  public static final String PRODUCER_KEY_SERIALIZER_NAME = "key.serializer";
  public static final String PRODUCER_VALUE_SERIALIZER_NAME = "value.serializer";
  public static final String PRODUICER_PARTITION_CLASS = "partitioner.class";
  public static final String PRODUCER_MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION = "max.in.flight.requests.per.connection";

  //consumer config
  public static final String CONSUMER_FETCH_MIN_BYTES = "fetch.min.bytes";
  public static final String CONSUMER_FETCH_MAX_WAIT_MS = "fetch.max.wait.ms";
  public static final String CONSUMER_MAX_PARTITION_FETCH_BYTES = "max.partition.fetch.bytes";
  public static final String CONSUMER_SESSION_TIMEOUT_MS = "session.timeout.ms";
  public static final String CONSUMER_AUTO_OFFSET_RESET = "auto.offset.reset";
  public static final String CONSUMER_AUTO_COMMIT = "enable.auto.commit";
  public static final String CONSUMER_PARTITION_ASSIGNMENT_STRATEGY = "partition.assignment.strategy";
  public static final String CONSUMER_MAX_POLL_RECORDS = "max.poll.records";
  public static final String CONSUMER_KEY_DESERIALIZER_NAME = "key.deserializer";
  public static final String CONSUMER_VALUE_DESERIALIZER_NAME = "value.deserializer";
  public static final String CONSUMER_GROUP_ID = "group.id";

  //permission related
  public static final String SECURITY_PROTOCOL = "security.protocol";
  public static final String SASL_KERBEROS_SERVICE_NAME = "sasl.kerberos.service.name";
  public static final String SASL_MECHANISM = "sasl.mechanism";
  public static final String SASL_JAAS_CONFIG = "sasl.jaas.config";
  public static final String SASL_JAAS_CONFIG_PARAPHRASE = "sasl.jaas.config.paraphrase";
  public static final String SASL_JAAS_CONFIG_STREAM  = "sasl.jaas.config.stream";

  //topic config
  public static final String TOPIC_NUM_PARTITIONS = "num.partitions";
  public static final String TOPIC_LOG_RETENTION_MS = "log.retention.ms";
  public static final String TOPIC_LOG_RETENTION_BYTES = "log.retention.bytes";
  public static final String TOPIC_LOG_SEGMENT_BYTES = "log.segment.bytes";
  public static final String TOPIC_LOG_SEGMENT_MS = "log.segment.ms";
  public static final String TOPIC_MESSAGE_MAX_BYTES = "message.max.bytes";

  //server config
  public static final String SERVER_ZOOKEEPER_CONNECT = "zookeeper.connect";

  public static Properties getNoSecuredAdminClientProperties(String bootstrapServerAddress) {
    Properties properties = new Properties();
    String address = parseBootstrapServerUrls(bootstrapServerAddress);
    if (address == null || address.isEmpty()) {
      throw new RuntimeException("Kafka bootstrap server address was not found.");
    }
    properties.put(BOOTSTRAP_SERVERS, address);
    return properties;
  }

  public static Properties getSecuredAdminClientProperties(String bootstrapServerAddress,
                                                           String kbServiceName, String principal, String keytabPath,
                                                           String principalStream, String keytabPathStream) {
    Properties properties = getNoSecuredAdminClientProperties(bootstrapServerAddress);
    if (StringUtils.isBlank(kbServiceName) || StringUtils.isBlank(principal) || StringUtils.isBlank(keytabPath) ||
            StringUtils.isBlank(principalStream) ||StringUtils.isBlank(keytabPathStream)) {
      throw new RuntimeException("认证信息配置不全,请重新检查");
    }
    properties.put(SECURITY_PROTOCOL, "SASL_PLAINTEXT");
    properties.put(SASL_KERBEROS_SERVICE_NAME, kbServiceName);
    properties.put(SASL_MECHANISM, "GSSAPI");
    properties.put(SASL_JAAS_CONFIG, getSaslJaasClientContent(principal, keytabPath));
    properties.put(SASL_JAAS_CONFIG_PARAPHRASE, getSaslJaasClientContentParaphrase(principal, keytabPath));
    properties.put(SASL_JAAS_CONFIG_STREAM,getSaslJaasClientContentParaphrase(principalStream, keytabPathStream));
    return properties;
  }

  public static Properties getDefaultProducerProperties(Properties adminClientProperties) {
    adminClientProperties.put(PRODUCER_KEY_SERIALIZER_NAME, DEFAULT_KEY_SERIALIZER);
    adminClientProperties.put(PRODUCER_VALUE_SERIALIZER_NAME, DEFAULT_VALUE_SERIALIZER);
    // 参数用于保证在出现重试时消息的顺序（默认为5,这里改为1保证强一致）
    //adminClientProperties.put(PRODUCER_MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
    // 降低延迟，降低吞吐（默认配置）
    //adminClientProperties.put(PRODUCER_LINGER_MS, 0);
    // 表示多少个副本收到消息，认为消息发送成功（默认leader收到即成功）
    //adminClientProperties.put(PRODUCER_ACKS, 1);
    // 幂等性参数，保证exactly once导入kafka  只针对kafka 0.11 版本之后
    //adminClientProperties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    // 压缩算法
    //adminClientProperties.put(PRODUCER_COMPRESSION_TYPE, "snappy");
    // 计算引擎部分使用的partitioner，保证分区一致
    adminClientProperties.put(PRODUICER_PARTITION_CLASS, HASH_PARTITIONER_CLASS);
    return adminClientProperties;
  }

  public static Properties getCustomProducerProperties(Properties adminClientProperties, Properties customProperties) {
    Properties finalProperties = getDefaultProducerProperties(adminClientProperties);
    Set<Map.Entry<Object, Object>> entrySet = customProperties.entrySet();
    for (Map.Entry<Object, Object> entry : entrySet) {
      finalProperties.put(entry.getKey(), entry.getValue());
    }

    return finalProperties;
  }

  private static String parseBootstrapServerUrls(String urls) {
    if (StringUtils.isBlank(urls)) return null;
    StringBuilder finalUrlsBuilder = new StringBuilder();
    for (String uri : urls.split(",")) {
      if (!uri.contains(":")) {
        finalUrlsBuilder.append(uri.trim()).append(":9092,");
      } else if (!StringUtils.isBlank(uri)) {
        finalUrlsBuilder.append(uri.trim()).append(",");
      }
    }
    String finalUris = finalUrlsBuilder.toString();
    return finalUris.substring(0, finalUris.length() - 1);
  }

  public static KafkaProducer<String, String> createProducer(Properties properties) throws Exception {
    if (properties == null || properties.isEmpty()) {
      throw new Exception("kafak producer config cannot be null");
    }
    return new KafkaProducer<>(properties);
  }

  private static String getSaslJaasClientContent(String principal, String keytabPath) {
    return " com.sun.security.auth.module.Krb5LoginModule required\n" +
            "  useKeyTab=true\n" +
            "  keyTab=\"" + keytabPath + "\"\n" +
            "  storeKey=true\n" +
            "  useTicketCache=false\n" +
            "  principal=\"" + principal + "\";\n";
  }

  //为引号里面的斜杠设置转义
  private static String getSaslJaasClientContentParaphrase (String principal, String keytabPath) {
    return " com.sun.security.auth.module.Krb5LoginModule required\n" +
            "  useKeyTab=true\n" +
            "  keyTab=\\\"" + keytabPath + "\\\"\n" +
            "  storeKey=true\n" +
            "  useTicketCache=false\n" +
            "  principal=\\\"" + principal + "\\\"\n";
  }
}
