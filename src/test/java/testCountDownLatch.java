import java.util.concurrent.*;

public class testCountDownLatch {
  public static void main(String[] args) {
    CountDownLatch countDownLatch = new CountDownLatch(5);
    BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>(5000);
    ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5,5, 1000,
            TimeUnit.SECONDS, queue);
    for(int i=0 ;i<5;i++) {
        poolExecutor.execute(new countDown(countDownLatch));
    }
    try {
      countDownLatch.await();
      poolExecutor.shutdown();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("task finish");
  }
}

class countDown implements Runnable{

  CountDownLatch countDownLatch;

  public countDown(CountDownLatch countDownLatch) {
    this.countDownLatch = countDownLatch;
  }

  @Override
  public void run() {
    try {
      Thread.sleep(5000);
      countDownLatch.countDown();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
