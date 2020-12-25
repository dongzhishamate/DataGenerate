package com.transwarp.generator.kafka.context;

import lombok.Data;

import java.util.Properties;

@Data
public class ThreadPoolContext {

  private Integer threadNum;
  private Integer tpsPerThread;

  public ThreadPoolContext(Properties properties) {
    this.threadNum = Integer.valueOf(properties.getProperty("threadNum"));
    this.tpsPerThread = Integer.valueOf(properties.getProperty("tpsPerThread"));
  }

}
