package com.transwarp.generator.kafka.context;

import lombok.Data;

@Data
public class KafkaContext {

  // kafka 连接以及安全相关  暂时不支持guardian token方式，因此使用keyTab
  private String bootstrapServerAddress;
  private String zkAddress;
  private boolean kafkaSecuredMode;
  private String saslKerberosServiceName;
  private String principal;
  private String principalStream;
  private String keytabPath;
  private String keytabPathStream;

  private int kafkaRefactor;
  private int kafkaConsumerTimeOut;
  private int intervalFlushTime;

  //是否永久执行数据插入
  private boolean isInsertUnlimited;

  //如果执行有限制的话设置插入的数据量
  private long maxNum;

  //是否开启client插入数据时的平均吞吐量计算，默认5s一次
  private Boolean startThroughputCalculate;

  //打印吞吐量的平均间隔
  private Integer ThroughputCalculateInterval;

}
