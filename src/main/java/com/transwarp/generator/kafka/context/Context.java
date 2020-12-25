package com.transwarp.generator.kafka.context;

import com.transwarp.generator.kafka.sendData;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@Data
public class Context {

  Logger LOG = LoggerFactory.getLogger(Context.class);

  KafkaContext kafkaContext;
  EmailContext emailContext;
  ThreadPoolContext threadPoolContext;
  SendMessageContext sendMessageContext;

  public Context(String path) {

    Properties properties = new Properties();
    try {
      BufferedReader bufferedReader = new BufferedReader
              (new FileReader(path));
      properties.load(bufferedReader);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    LOG.info(properties.toString());

    if(Boolean.valueOf(properties.getProperty("sendEmail"))) {
      emailContext = new EmailContext(properties);
    }

    kafkaContext = new KafkaContext(properties);
    sendMessageContext = new SendMessageContext(properties);
    threadPoolContext = new ThreadPoolContext(properties);
  }

}
