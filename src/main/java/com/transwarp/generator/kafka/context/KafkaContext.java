package com.transwarp.generator.kafka.context;

import lombok.Data;

import java.util.Properties;

@Data
public class KafkaContext {

  private String bootstrapServerAddress;
  private String ZkAddress;
  private String kafkaTopic;
  private Integer kafkaRefactor = 3;
  private Integer intervalFlushTime;
  private String compressionCodec = "snappy";
  private String maxInFlightRequestsPerConnection = "5";
  private Integer batchSize = 16384;
  private Integer lingerMs = 100;
  private Long bufferSize = 33554432L;
  private String ack = "1";

  public KafkaContext(Properties properties) {
    this.bootstrapServerAddress = String.valueOf(properties.getProperty("bootstrapServerAddress"));
    this.compressionCodec = String.valueOf(properties.getProperty("compressionCodec"));
    this.kafkaTopic = properties.getProperty("kafkaTopic");
    //kafka的max.in.flight.requests.per.connection属性，默认是5，如果要保证消息的正确性那要设为1
    this.maxInFlightRequestsPerConnection = String.valueOf(properties.getProperty("maxInFlightRequestsPerConnection"));
    this.batchSize = Integer.valueOf(properties.getProperty("batchSize"));
    //linger.ms默认是0
    this.lingerMs = Integer.valueOf(properties.getProperty("lingerMs"));
    //缓冲区的大小
    this.bufferSize = Long.valueOf(properties.getProperty("bufferSize"));
    //ACK
    String ack = properties.getProperty("ack");
  }
}
