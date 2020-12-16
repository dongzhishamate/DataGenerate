package com.transwarp.generator.kafka.partitioners;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class KafkaHashPartitioner implements Partitioner {
  private static Logger LOG = LoggerFactory.getLogger(KafkaHashPartitioner.class);

  public KafkaHashPartitioner() {
  }

  @Override
  public void configure(Map<String, ?> configs) {

  }

  // copy code from core engine
  @Override
  public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
    List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
    int numPartitions = partitions.size();
    if (key == null) {
      return 0;
    } else {
      int rawMod = key.hashCode() % numPartitions;
      if (rawMod < 0) {
        return numPartitions + rawMod;
      } else return rawMod;
    }
  }

  @Override
  public void close() {

  }
}
