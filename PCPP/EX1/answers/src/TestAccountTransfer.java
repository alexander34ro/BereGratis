BasicJava/TestLocking0.java                                                                         000644  000770  000024  00000001461 13525024001 016153  0                                                                                                    ustar 00thdy                            staff                           000000  000000                                                                                                                                                                         // For week 1
// sestoft@itu.dk * 2015-10-29

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
  private static double sum = 0;

  public static synchronized void addStatic(double x) {
    sum += x;
  }

  public synchronized void addInstance(double x) {
    sum += x;
  }

  public static synchronized double sum() {
    return sum;
  }
}
                                                                                                                                                                                                               BasicJava/TestLocking1.java                                                                         000644  000770  000024  00000003326 13525024001 016156  0                                                                                                    ustar 00thdy                            staff                           000000  000000                                                                                                                                                                         // For week 1
// sestoft@itu.dk * 2015-10-29

public class TestLocking1 {
  public static void main(String[] args) {
    DoubleArrayList dal1 = new DoubleArrayList();
    dal1.add(42.1); dal1.add(7.2); dal1.add(9.3); dal1.add(13.4); 
    dal1.set(2, 11.3);
    for (int i=0; i<dal1.size(); i++)
      System.out.println(dal1.get(i));
    DoubleArrayList dal2 = new DoubleArrayList();
    dal2.add(90.1); dal2.add(80.2); dal2.add(70.3); dal2.add(60.4); dal2.add(50.5); 
    DoubleArrayList dal3 = new DoubleArrayList();
  }
}

class DoubleArrayList {
  // Invariant: 0 <= size <= items.length
  private double[] items = new double[2];
  private int size = 0;

  // Number of items in the double list
  public int size() {
    return size;
  }

  // Return item number i, if any
  public double get(int i) {
    if (0 <= i && i < size) 
      return items[i];
    else 
      throw new IndexOutOfBoundsException(String.valueOf(i));
  }

  // Add item x to end of list
  public boolean add(double x) {
    if (size == items.length) {
      double[] newItems = new double[items.length * 2];
      for (int i=0; i<items.length; i++)
	newItems[i] = items[i];
      items = newItems;
    }
    items[size] = x;
    size++;
    return true;
  }

  // Replace item number i, if any, with x
  public double set(int i, double x) {
    if (0 <= i && i < size) {
      double old = items[i];
      items[i] = x;
      return old;
    } else 
      throw new IndexOutOfBoundsException(String.valueOf(i));
  }

  // The double list formatted as eg "[3.2, 4.7]"
  public String toString() {
    StringBuilder sb = new StringBuilder("[");
    for (int i=0; i<size; i++)
      sb.append(i > 0 ? ", " : "").append(items[i]);
    return sb.append("]").toString();
  }
}
                                                                                                                                                                                                                                                                                                          BasicJava/TestLocking2.java                                                                         000644  000770  000024  00000004305 13525024001 016155  0                                                                                                    ustar 00thdy                            staff                           000000  000000                                                                                                                                                                         // For week 1
// sestoft@itu.dk * 2015-10-29

import java.util.HashSet;

public class TestLocking2 {
  public static void main(String[] args) {
    DoubleArrayList dal1 = new DoubleArrayList();
    dal1.add(42.1); dal1.add(7.2); dal1.add(9.3); dal1.add(13.4); 
    dal1.set(2, 11.3);
    for (int i=0; i<dal1.size(); i++)
      System.out.println(dal1.get(i));
    DoubleArrayList dal2 = new DoubleArrayList();
    dal2.add(90.1); dal2.add(80.2); dal2.add(70.3); dal2.add(60.4); dal2.add(50.5); 
    DoubleArrayList dal3 = new DoubleArrayList();
    System.out.printf("Total size = %d%n", DoubleArrayList.totalSize());
    System.out.printf("All lists  = %s%n", DoubleArrayList.allLists());
  }
}

class DoubleArrayList {
  private static int totalSize = 0;
  private static HashSet<DoubleArrayList> allLists = new HashSet<>();

  // Invariant: 0 <= size <= items.length
  private double[] items = new double[2];
  private int size = 0;

  public DoubleArrayList() {
    allLists.add(this);
  }

  // Number of items in the double list
  public int size() {
    return size;
  }

  // Return item number i, if any
  public double get(int i) {
    if (0 <= i && i < size) 
      return items[i];
    else 
      throw new IndexOutOfBoundsException(String.valueOf(i));
  }

  // Add item x to end of list
  public boolean add(double x) {
    if (size == items.length) {
      double[] newItems = new double[items.length * 2];
      for (int i=0; i<items.length; i++)
	newItems[i] = items[i];
      items = newItems;
    }
    items[size] = x;
    size++;
    totalSize++;
    return true;
  }

  // Replace item number i, if any, with x
  public double set(int i, double x) {
    if (0 <= i && i < size) {
      double old = items[i];
      items[i] = x;
      return old;
    } else 
      throw new IndexOutOfBoundsException(String.valueOf(i));
  }

  // The double list formatted as eg "[3.2, 4.7]"
  public String toString() {
    StringBuilder sb = new StringBuilder("[");
    for (int i=0; i<size; i++)
      sb.append(i > 0 ? ", " : "").append(items[i]);
    return sb.append("]").toString();
  }

  public static int totalSize() {
    return totalSize;
  }

  public static HashSet<DoubleArrayList> allLists() {
    return allLists;
  }
}
                                                                                                                                                                                                                                                                                                                           BasicJava/TestLocking3.java                                                                         000644  000770  000024  00000001613 13525024001 016155  0                                                                                                    ustar 00thdy                            staff                           000000  000000                                                                                                                                                                         // For week 1
// sestoft@itu.dk * 2016-09-01

public class TestLocking3 {
  public static void main(String[] args) {
    final int counts = 10_000_000;
    Thread t1 = new Thread(() -> {
      for (int i=0; i<counts; i++) 
	MysteryB.increment();
    });
    Thread t2 = new Thread(() -> {
      for (int i=0; i<counts; i++) 
	MysteryB.increment4();
    });
    t1.start(); t2.start();
    try { t1.join(); t2.join(); }
    catch (InterruptedException exn) { 
      System.out.println("Some thread was interrupted");
    }
    System.out.println("Count is " + MysteryA.get() + " and should be " + 5*counts);
  }
}

class MysteryA {
  protected static long count = 0;

  public static synchronized void increment() {
    count++;
  }

  public static synchronized long get() { 
    return count; 
  }
}

class MysteryB extends MysteryA {
  public static synchronized void increment4() {
    count += 4;
  }
}
                                                                                                                     BasicJava/TestLongCounter.java                                                                      000644  000770  000024  00000001266 13525024001 016747  0                                                                                                    ustar 00thdy                            staff                           000000  000000                                                                                                                                                                         // sestoft@itu.dk * 2014-08-20, 2015-08-27

import java.io.IOException;

public class TestLongCounter {
  public static void main(String[] args) throws IOException {
    final LongCounter lc = new LongCounter();
    Thread t = new Thread(() -> {
	while (true)		// Forever call increment
	  lc.increment();
      });
    t.start();
    System.out.println("Press Enter to get the current value:");
    while (true) {
      System.in.read();         // Wait for enter key
      System.out.println(lc.get()); 
    }
  }
}

class LongCounter {
  private long count = 0;
  public synchronized void increment() {
    count = count + 1;
  }
  public synchronized long get() { 
    return count; 
  }
}
                                                                                                                                                                                                                                                                                                                                          BasicJava/TestLongCounter7.java                                                                     000644  000770  000024  00000001361 13525024001 017032  0                                                                                                    ustar 00thdy                            staff                           000000  000000                                                                                                                                                                         // For week 1
// sestoft@itu.dk * 2014-08-20

// java 7 version

import java.io.IOException;

public class TestLongCounter7 {
  public static void main(String[] args) throws IOException {
    final LongCounter lc = new LongCounter();
    Thread t = new Thread(new Runnable() {
	public void run() {
	  while (true)		// Forever call increment
	    lc.increment();
	}
      });
    t.start();
    System.out.println("Press Enter to get the current value:");
    while (true) {
      System.in.read();         // Wait for enter key
      System.out.println(lc.get()); 
    }
  }
}

class LongCounter {
  private long count = 0;
  public synchronized void increment() {
    count = count + 1;
  }
  public synchronized long get() { 
    return count; 
  }
}
                                                                                                                                                                                                                                                                               BasicJava/TestLongCounterBetter.java                                                                000644  000770  000024  00000001462 13525024001 020113  0                                                                                                    ustar 00thdy                            staff                           000000  000000                                                                                                                                                                         // For week 1
// sestoft@itu.dk * 2016-09-01

import java.io.IOException;

public class TestLongCounterBetter {
  public static void main(String[] args) throws IOException {
    final LongCounterBetter lc = new LongCounterBetter();
    Thread t = new Thread(() -> {
	while (true)		// Forever call increment
	  lc.increment();
      });
    t.start();
    System.out.println("Press Enter to get the current value:");
    while (true) {
      System.in.read();         // Wait for enter key
      System.out.println(lc.get()); 
    }
  }
}

class LongCounterBetter {
  private long count = 0;
  private final Object myLock = new Object();
  public void increment() {
    synchronized (myLock) {
      count = count + 1;
    }
  }
  public long get() { 
    synchronized (myLock) {
      return count;       
    }
  }
}
                                                                                                                                                                                                              BasicJava/TestLongCounterExperiments.java                                                           000644  000770  000024  00000001462 13525024001 021171  0                                                                                                    ustar 00thdy                            staff                           000000  000000                                                                                                                                                                         // For week 1
// sestoft@itu.dk * 2014-08-21

public class TestLongCounterExperiments {
  public static void main(String[] args) {
    final LongCounter lc = new LongCounter();
    final int counts = 10_000_000;
    Thread t1 = new Thread(() -> {
      for (int i=0; i<counts; i++) 
	lc.increment();
    });
    Thread t2 = new Thread(() -> {
      for (int i=0; i<counts; i++) 
	lc.increment();
    });
    t1.start(); t2.start();
    try { t1.join(); t2.join(); }
    catch (InterruptedException exn) { 
      System.out.println("Some thread was interrupted");
    }
    System.out.println("Count is " + lc.get() + " and should be " + 2*counts);
  }
}

class LongCounter {
  private long count = 0;
  public synchronized void increment() {
    count = count + 1;
  }
  public  long get() { 
    return count; 
  }
}
                                                                                                                                                                                                              BasicJava/TestLongCounterNaiv.java                                                                  000644  000770  000024  00000001325 13525024001 017561  0                                                                                                    ustar 00thdy                            staff                           000000  000000                                                                                                                                                                         // For week 1
// rikj@itu.dk 2017-08-14 ; sestoft@itu.dk * 2014-08-20, 2015-08-27

import java.io.IOException;

public class TestLongCounterNaiv {
  public static void main(String[] args) throws IOException {
    final LongCounter lc = new LongCounter();
    Thread t = new Thread(() -> {
	while (true)		// Forever call increment
	  lc.increment();
      });
    t.start();
    System.out.println("Press Enter to get the current value:");
    while (true) {
      System.in.read();         // Wait for enter key
      long 
      System.out.println(lc.get()); 
    }
  }
}

class LongCounter {
  private long count = 0;
  public  void increment() {
    count = count + 1;
  }
  public  long get() { 
    return count; 
  }
}
                                                                                                                                                                                                                                                                                                           BasicJava/TestMutableInteger.java                                                                   000644  000770  000024  00000002002 13525024001 017404  0                                                                                                    ustar 00thdy                            staff                           000000  000000                                                                                                                                                                         // For week 1
// sestoft@itu.dk * 2014-08-25

import java.io.IOException;

public class TestMutableInteger {
  public static void main(String[] args) throws IOException {
    final MutableInteger mi = new MutableInteger();
    Thread t = new Thread(() -> {
        while (mi.get() == 0)        // Loop while zero
          { }
        System.out.println("I completed, mi = " + mi.get()); 
      });
    t.start();
    System.out.println("Press Enter to set mi to 42:");
    System.in.read();                   // Wait for enter key
    mi.set(42);
    System.out.println("mi set to 42, waiting for thread ..."); 
    try { t.join(); } catch (InterruptedException exn) { }
    System.out.println("Thread t completed, and so does main"); 
  }
}

// From Goetz p. 36.  WARNING: Useless without "volatile" or
// "synchronized" to ensure visibility of writes across threads:
class MutableInteger {
  private int value = 0;   
  public void set(int value) {
    this.value = value;
  }
  public int get() {
    return value; 
  }
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              