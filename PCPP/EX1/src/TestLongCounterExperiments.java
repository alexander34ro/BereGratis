// For week 1
// sestoft@itu.dk * 2014-08-21

public class TestLongCounterExperiments {
  public static void main(String[] args) {
    final LongCounterEx lc = new LongCounterEx();
    final int counts = 10_000_000;
    Thread t1 = new Thread(() -> {
      for (int i=0; i<counts; i++) 
	lc.increment();
    });
    Thread t2 = new Thread(() -> {
      for (int i=0; i<counts; i++) 
	lc.decrement();
    });
    t1.start(); t2.start();
    try { t1.join(); t2.join(); }
    catch (InterruptedException exn) { 
      System.out.println("Some thread was interrupted");
    }
    System.out.println("Count is " + lc.get() + " and should be " + 0);
  }
}

class LongCounterEx {
  private long count = 0;
  public  void increment() {
    count++;
  }
  public  void decrement() {
	    count--;
	  }
  public synchronized long get() { 
    return count; 
  }
}
