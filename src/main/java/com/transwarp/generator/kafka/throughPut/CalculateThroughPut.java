package com.transwarp.generator.kafka.throughPut;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class CalculateThroughPut implements Runnable {

  public static AtomicLong sum = new AtomicLong(0);
  public static boolean isStop = false;

  SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");

  public static void add() {
    sum.incrementAndGet();
  }


  @Override
  public void run() {
    while (true) {
      if(isStop) {
        break;
      }
      try {
        Thread.sleep(5000);
        System.out.println("[CalculateThroughPut] " + sum.get() / 5.0 + " ,time= ," + sdFormat.format(new Date()));
        sum.set(0);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
