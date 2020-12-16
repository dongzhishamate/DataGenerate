package com.transwarp.generator.kafka.currentlimiting;

import java.util.concurrent.atomic.AtomicLong;

public class Limiting implements Runnable{

  public static AtomicLong  currentNum = new AtomicLong(0);
  public static int limit = 10000;
  public static boolean isClose = false;

  public Limiting(int limit) {
    Limiting.limit = limit;
  }

  public static boolean grant() {
    if(currentNum.get() < limit) {
      currentNum.incrementAndGet();
      return true;
    }
    else {
      return false;
    }
  }

  @Override
  public void run() {
    while(true) {
      if(isClose) {
        break;
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      currentNum.set(0);
    }
  }
}
