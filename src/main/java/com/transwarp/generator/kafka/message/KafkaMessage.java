package com.transwarp.generator.kafka.message;

import com.transwarp.generator.kafka.utils.StringUtils;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KafkaMessage {

  public static ProducerRecord genKafkaRecord(String kafkaTopic ,
                                              int columnNum , boolean isPrint,
                                              boolean isPartition, List<Integer> partitionIndex) {
    List<String> values = new ArrayList<>();
    values = getRecord(columnNum, isPartition, partitionIndex);
    String isDeletaAndTime = "";
    if(isPrint) {
      isDeletaAndTime = "false|" + System.currentTimeMillis();
    } else {
      isDeletaAndTime = "false|";
    }
    values.add(isDeletaAndTime);
    String valuesString = StringUtils.zip(values, ",");

    ProducerRecord record = new ProducerRecord<String, String>(kafkaTopic, values.get(0), valuesString);
    return record;
  }

  public static List<String> getRecord(int columnNum, boolean isPartition,
                                       List<Integer> partitionIndex) {
    if(isPartition) {
      return genRecordWithPartitions(columnNum, partitionIndex);
    } else {
      return genRecord(columnNum);
    }
  }

  public static List<String> genRecordWithPartitions(int columnNum,
                                                     List<Integer> partitionIndex) {
    List<String> record = new ArrayList<>();
    record.add(UUID.randomUUID().toString());
    for(int i = 1 ; i < columnNum ; i++) {
      record.add("aaaaaaaa");
    }
    for(int index : partitionIndex) {
      record.set(index, String.valueOf(Math.random() * 100));
    }
    return record;
  }

  public static List<String> genRecord(int columnNum) {
    List<String> record = new ArrayList<>();
    record.add(UUID.randomUUID().toString());
    for(int i = 1 ; i < columnNum ; i++) {
      record.add("aaaaaaaa");
    }
    return record;
  }
}
