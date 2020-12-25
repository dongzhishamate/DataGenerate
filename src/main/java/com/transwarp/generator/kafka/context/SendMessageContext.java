package com.transwarp.generator.kafka.context;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Data
public class SendMessageContext {

  private Boolean isInsertUnlimited;
  private Long maxNum;
  Boolean startThroughputCalculate;
  Integer ThroughputCalculateInterval;
  Integer columnNum;
  Boolean isLimiting;
  Integer tps;
  Boolean isPartition;
  Boolean isKunDB;
  Boolean isAutoColumnType;
  List<Integer> partitionIndex;
  private Boolean sendEmail;

  public SendMessageContext(Properties properties) {
    this.isInsertUnlimited = Boolean.valueOf(properties.getProperty("isInsertUnlimited"));
    this.maxNum = Long.valueOf(properties.getProperty("maxNum"));
    this.startThroughputCalculate = Boolean.valueOf(properties.getProperty("startThroughputCalculate"));
    this.ThroughputCalculateInterval = Integer.valueOf(properties.getProperty("ThroughputCalculateInterval"));
    this.columnNum = Integer.valueOf(properties.getProperty("columnNum"));
    this.isLimiting = Boolean.valueOf(properties.getProperty("isLimiting"));
    this.tps = Integer.valueOf(properties.getProperty("tps"));
    this.isPartition = Boolean.valueOf(properties.getProperty("isPartition"));
    //生成的数据为kundb的数据
    this.isKunDB = Boolean.valueOf(properties.getProperty("isKunDB"));
    //是否自动设定各个列属性的类型，如果为true就默认为String，不然得自己设定
    this.isAutoColumnType = Boolean.valueOf(properties.getProperty("isAutoColumnType"));
    //分区的column
    List<Integer> partitionIndex = new ArrayList<Integer>(0);
    if(properties.getProperty("partitionIndex") != null) {
      partitionIndex = Arrays.stream(properties.getProperty("partitionIndex").split(",")).
              map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());
    }
    this.sendEmail = Boolean.valueOf(properties.getProperty("sendEmail"));
  }

}
