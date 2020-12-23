package com.transwarp.generator.kafka;


import com.transwarp.generator.kafka.ThroughPut.calculateThroughPut;
import com.transwarp.generator.kafka.currentlimiting.Limiting;
import com.transwarp.generator.kafka.kafka.KafkaSendClient;
import com.transwarp.generator.kafka.email.MailConfig;
import com.transwarp.generator.kafka.email.MailSender;
import com.transwarp.generator.kafka.message.KafkaMessage;
import com.transwarp.generator.kafka.message.KunDB;
import com.transwarp.generator.kafka.partitioners.KafkaHashPartitioner;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class sendData {

  public static void main(String[] args) {

    Logger LOG = LoggerFactory.getLogger(sendData.class);

    SpringApplication.run(sendData.class,args);

    //D:\实习汇总\星环实习\项目\DataGenerate\config_kunDB.properties
    Properties properties = new Properties();
    try {
      BufferedReader bufferedReader = new BufferedReader
              (new FileReader("D:\\实习汇总\\星环实习\\项目\\DataGenerate\\configKafka.properties"));
      properties.load(bufferedReader);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println(properties.toString());

    String compressionCodec = String.valueOf(properties.getProperty("compressionCodec"));
    boolean isInsertUnlimited = Boolean.valueOf(properties.getProperty("isInsertUnlimited"));
    long maxNum = Long.valueOf(properties.getProperty("maxNum"));
    boolean startThroughputCalculate = Boolean.valueOf(properties.getProperty("startThroughputCalculate"));
    int ThroughputCalculateInterval = Integer.valueOf(properties.getProperty("ThroughputCalculateInterval"));
    String kafkaTopic = properties.getProperty("kafkaTopic");
    int columnNum = Integer.valueOf(properties.getProperty("columnNum"));
    boolean isLimiting = Boolean.valueOf(properties.getProperty("isLimiting"));
    int tps = Integer.valueOf(properties.getProperty("tps"));
    boolean isPartition = Boolean.valueOf(properties.getProperty("isPartition"));
    //生成的数据为kundb的数据
    boolean isKunDB = Boolean.valueOf(properties.getProperty("isKunDB"));
    //kafka的max.in.flight.requests.per.connection属性，默认是5，如果要保证消息的正确性那要设为1
    String maxInFlightRequestsPerConnection = String.valueOf(properties.getProperty("maxInFlightRequestsPerConnection"));
    //是否自动设定各个列属性的类型，如果为true就默认为String，不然得自己设定
    boolean isAutoColumnType = Boolean.valueOf(properties.getProperty("isAutoColumnType"));
    boolean sendEmail = Boolean.valueOf(properties.getProperty("sendEmail"));
    MailSender mailSender = null;
    if (sendEmail) {
      String senderAddress = String.valueOf(properties.getProperty("senderAddress"));
      String recipientAddress = String.valueOf(properties.getProperty("recipientAddress"));
      String senderAccount = String.valueOf(properties.getProperty("senderAccount"));
      String senderPassword = String.valueOf(properties.getProperty("senderPassword"));
      String mailSmtpAuth = String.valueOf(properties.getProperty("mailSmtpAuth"));
      String mailTransportProtocol = String.valueOf(properties.getProperty("mailTransportProtocol"));
      String mailStmpHost = String.valueOf(properties.getProperty("mailStmpHost"));
      mailSender = new MailSender(new MailConfig(senderAddress, recipientAddress, senderAccount,
              senderPassword, mailSmtpAuth, mailTransportProtocol, mailStmpHost));
    }



    List<Integer> partitionIndex = new ArrayList<Integer>(0);
    if(properties.getProperty("partitionIndex") != null) {
      partitionIndex = Arrays.stream(properties.getProperty("partitionIndex").split(",")).
              map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());
    }

    if(isAutoColumnType) {

    }


    Long startTime = System.currentTimeMillis();
    Long index = 0L;
    System.out.println("start = " + new Date());
    //计算client吞吐
    Thread thread = new Thread(new calculateThroughPut());
    thread.start();

    Properties kafkaProperties = new Properties();

    kafkaProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,properties.getProperty("bootstrapServerAddress"));
    kafkaProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
    kafkaProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
    kafkaProperties.put(ProducerConfig.ACKS_CONFIG,"1");
    kafkaProperties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, KafkaHashPartitioner.class);
    kafkaProperties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionCodec);
    if(maxInFlightRequestsPerConnection != null){
      kafkaProperties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlightRequestsPerConnection);
    }

    KafkaSendClient kafkaClient = new KafkaSendClient(kafkaProperties);
    KafkaProducer kafkaProducer = kafkaClient.getProducer();

    //限流
    if(isLimiting){
      Limiting limiting =new Limiting(tps);
      Thread thread1 = new Thread(limiting);
      thread1.start();
    }
    ProducerRecord record;

    KafkaMessage kafkaMessage = new KafkaMessage();
    KunDB kunDB = new KunDB(maxNum);

    while (true) {
      if(!isInsertUnlimited) {
        if(index == maxNum) {
          kafkaProducer.flush();
          calculateThroughPut.isStop = true;
          System.out.println("send "+maxNum+" message finished ,now at :"+ new Date() +" ,spend time : " +
                  (System.currentTimeMillis() - startTime));
          //发送email
          if(sendEmail) {
            try {
              mailSender.sendMessage("send message finish at : " + new Date());
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
          //close会阻塞等待之前所有的发送请求完成之后再关闭
          kafkaProducer.close();
          break;
        }
      }

      if(isLimiting && !Limiting.grant()){
        continue;
      }
      else {
        if(startThroughputCalculate && (index % ThroughputCalculateInterval == 0 || (index == maxNum))){
          if(isKunDB) {
            record = kunDB.genKafkaRecord(kafkaTopic, true);
          } else{
            record = kafkaMessage.genKafkaRecord(kafkaTopic ,columnNum ,true, isPartition, partitionIndex);
          }
        } else{
          if(isKunDB) {
            record = kunDB.genKafkaRecord(kafkaTopic, false);
          } else {
            record = kafkaMessage.genKafkaRecord(kafkaTopic , columnNum ,false, isPartition, partitionIndex);
          }
        }
        index ++;
        calculateThroughPut.sum ++;

        kafkaProducer.send(record, new Callback() {
          @Override
          public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e != null) {
              LOG.error("producer failed. Exception [{}].", e);
              throw new RuntimeException("producer failed.", e);
            }
          }
        });

        if(index%50000 == 0) {
          kafkaProducer.flush();
        }
      }
    }
  }
}
