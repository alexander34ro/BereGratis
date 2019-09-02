// For week 1
// sestoft@itu.dk * 2015-10-29

import java.util.concurrent.locks.ReentrantLock;

public class TestLocking0 {
  public static void main(String[] args) {
    final int count = 1_000_000;
    Mystery m = new Mystery();
    Thread t1 = new Thread(() -> {
      for (int i=0; i<count; i++)
        m.addInstance(1);
    });
    Thread t2 = new Thread(() -> {
      for (int i=0; i<count; i++)
        m.addStatic(1);
    });
    t1.start(); t2.start();
    try { t1.join(); t2.join(); } catch (InterruptedException exn) { }
    System.out.printf("Sum is %f and should be %f%n", m.sum(), 2.0 * count);
  }
}

class Mystery {
  private static ReentrantLock l = new ReentrantLock();
  private static double sum = 0;

  public static void addStatic(double x) {
    l.lock();
    sum += x;
    l.unlock();
  }

  public void addInstance(double x) {
    l.lock();
    sum += x;
    l.unlock();
  }

  public static double sum() {
    l.lock();
    double s = sum;
    l.unlock();
    return s;
  }
}