package com.transwarp.generator.kafka.ThroughPut;


import java.text.SimpleDateFormat;
import java.util.Date;

public class calculateThroughPut implements Runnable {

  public static Long sum = 0L;
  public static boolean isStop = false;

  SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

  public static void add() {
    sum++;
  }


  @Override
  public void run() {
    while (true) {
      if(isStop) {
        break;
      }
      try {
        Thread.sleep(5000);
        System.out.println("[calculateThroughPut]" + sum / 5.0 + " ,time = " + sdFormat.format(new Date()));
        sum = 0L;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
