package com.transwarp.generator.kafka;

import com.transwarp.generator.kafka.context.Context;
import com.transwarp.generator.kafka.context.KafkaContext;
import com.transwarp.generator.kafka.context.SendMessageContext;
import com.transwarp.generator.kafka.context.ThreadPoolContext;
import com.transwarp.generator.kafka.email.MailSender;
import com.transwarp.generator.kafka.message.KunDB;
import com.transwarp.generator.kafka.throughPut.CalculateThroughPut;
import com.transwarp.generator.kafka.currentlimiting.Limiting;
import com.transwarp.generator.kafka.kafka.KafkaSendClient;
import com.transwarp.generator.kafka.message.KafkaMessage;
import org.apache.kafka.clients.producer.*;

import java.util.*;
import java.util.concurrent.*;

public class sendData {

  public static void main(String[] args) {

//    Logger LOG = LoggerFactory.getLogger(sendData.class);

    String path = "/root/zfy/DataGenerate2.0/config.properties";
    if(args.length != 0){
      path = args[0];
    }
    Context context = new Context(path);
    ThreadPoolContext threadPoolContext = context.getThreadPoolContext();
    SendMessageContext sendMessageContext = context.getSendMessageContext();
    KafkaContext kafkaContext = context.getKafkaContext();

    Long startTime = System.currentTimeMillis();
    System.out.println("start = " + new Date());
    //计算client吞吐
    Thread thread = new Thread(new CalculateThroughPut());
    thread.start();

    try {
      KafkaSendClient kafkaSendClient = new KafkaSendClient(context.getKafkaContext());

      //创建线程池,countDownLatch标记剩余任务数
      CountDownLatch countDownLatch = new CountDownLatch(threadPoolContext.getThreadNum());
      BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>(100);
      ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(threadPoolContext.getThreadNum(),
              threadPoolContext.getThreadNum(), 1000,
              TimeUnit.SECONDS, queue);

      for (int i=0;i<threadPoolContext.getThreadNum();i++) {
        KafkaProducer kafkaProducer = kafkaSendClient.getProducer();
        InsertDataTask insertDataTask = new InsertDataTask(sendMessageContext, kafkaProducer, startTime,
                kafkaContext.getKafkaTopic(), countDownLatch, threadPoolContext);
        System.out.println("task "+i+"start");
        poolExecutor.execute(insertDataTask);
      }

      //等所有任务结束之后发送email
      try {
        countDownLatch.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if(sendMessageContext.getSendEmail()) {
        MailSender mailSender = new MailSender(context.getEmailContext());
        try {
          mailSender.sendMessage("send message finish at : " + new Date());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }catch (Exception e) {
      e.printStackTrace();
      CalculateThroughPut.isStop = true;
    }

  }
}

class InsertDataTask implements Runnable {

//  Logger LOG = LoggerFactory.getLogger(InsertDataTask.class);

  private Boolean isLimiting = false;
  //当前数据量
  private Long index = 0l;
  private KafkaProducer kafkaProducer;
  private Long startTime;
  private SendMessageContext sendMessageContext;
  private ThreadPoolContext threadPoolContext;
  private String kafkaTopic;
  CountDownLatch countDownLatch;

  public InsertDataTask(SendMessageContext sendMessageContext, KafkaProducer kafkaProducer, long startTime,
                        String kafkaTopic, CountDownLatch countDownLatch, ThreadPoolContext threadPoolContext) {
    this.sendMessageContext = sendMessageContext;
    this.startTime = startTime;
    this.kafkaProducer = kafkaProducer;
    this.kafkaTopic = kafkaTopic;
    this.countDownLatch = countDownLatch;
    this.threadPoolContext = threadPoolContext;
  }

  @Override
  public void run() {
    //限流
    if(isLimiting){
      Limiting limiting =new Limiting(sendMessageContext.getTps());
      Thread thread1 = new Thread(limiting);
      thread1.start();
    }
    ProducerRecord record;

    KafkaMessage kafkaMessage = new KafkaMessage();
//    KunDB kunDB = new KunDB(maxNum);

    while (true) {
      if(!sendMessageContext.getIsInsertUnlimited()) {
        if(index == sendMessageContext.getMaxNum()) {
          kafkaProducer.flush();
          CalculateThroughPut.isStop = true;
          System.out.println("send "+ sendMessageContext.getMaxNum() +" message finished ,now at :"+ new Date() +" ," +
                  "spend time : " +
                  (System.currentTimeMillis() - startTime));
          //close会阻塞等待之前所有的发送请求完成之后再关闭
          kafkaProducer.close();
          countDownLatch.countDown();
          break;
        }
      }

      if(isLimiting && !Limiting.grant()){
        continue;
      }
      else {
        if(sendMessageContext.getStartThroughputCalculate() &&
                (index % sendMessageContext.getThroughputCalculateInterval() == 0 || (index == sendMessageContext.getMaxNum()))){
          if(sendMessageContext.getIsKunDB()) {
            record = KunDB.genKafkaRecord(kafkaTopic, true);
          } else{
            record = kafkaMessage.genKafkaRecord(kafkaTopic ,sendMessageContext.getColumnNum() ,true,
                    sendMessageContext.getIsPartition(),
                    sendMessageContext.getPartitionIndex());
          }
        } else{
          if(sendMessageContext.getIsKunDB()) {
            record = KunDB.genKafkaRecord(kafkaTopic, false);
          } else {
            record = kafkaMessage.genKafkaRecord(kafkaTopic , sendMessageContext.getColumnNum(),false,
                    sendMessageContext.getIsPartition(),
                    sendMessageContext.getPartitionIndex());
          }
        }
        index ++;
        CalculateThroughPut.add();

        kafkaProducer.send(record, new Callback() {
          @Override
          public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e != null) {
//              LOG.error("producer send failed. Exception [{}].", e);
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
