// Week 3
// sestoft@itu.dk * 2015-09-09

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.stream.*;

public class TestWordStream {
  public static void main(String[] args) {
    //String filename = "/usr/share/dict/words";
    String filename = "words";
    // 4.4.1
    System.out.println("4.4.1");
    System.out.println(readWords(filename).count());
    // 4.4.2
    System.out.println("4.4.2");
    readWords(filename).limit(100).forEach(System.out::println);
    // 4.4.3
    System.out.println("4.4.3");
    readWords(filename).filter((x) -> x.length() >= 22).forEach(System.out::println);
    // 4.4.4
    System.out.println("4.4.4");
    readWords(filename).filter((x) -> x.length() >= 22).findAny().ifPresent(System.out::println);
    // 4.4.5
    System.out.println("4.4.5");
    readWords(filename).filter(TestWordStream::isPalindrome).forEach(System.out::println);
    // 4.4.6
    System.out.println("4.4.6");
    readWords(filename).parallel().filter(TestWordStream::isPalindrome).forEach(System.out::println);
    // 4.4.7
    System.out.println("4.4.7");
    IntSummaryStatistics s = readWords(filename).mapToInt((x) -> (int)x.chars().count()).summaryStatistics();
    System.out.println("Min: " + s.getMin());
    System.out.println("Max: " + s.getMax());
    System.out.println("Avg: " + s.getAverage());
    // 4.4.8
    System.out.println("4.4.8");
    readWords(filename);
    // 4.4.9
    System.out.println("4.4.9");
    readWords(filename);
    // 4.4.10
    System.out.println("4.4.10");
    readWords(filename);
    // 4.4.11
    System.out.println("4.4.11");
    readWords(filename);
    // 4.4.12
    System.out.println("4.4.12");
    readWords(filename);
    // 4.4.13
    System.out.println("4.4.13");
    readWords(filename);

    // *********************************
    // DoubleStreams
    int range = 1_000_000_000;
    // 4.5.1
    final DoubleStream doubleStream1 = IntStream.range(1, range).mapToDouble((x) -> 1.0 / x / x);
    final double sum1 = doubleStream1.sum();
    System.out.printf("Sum = %20.16f%n", sum1 - Math.PI * Math.PI / 6);

    // 4.5.2
    final DoubleStream doubleStream2 = IntStream.range(1, range).parallel().mapToDouble((x) -> 1.0 / x / x);
    final double sum2 = doubleStream2.parallel().sum();
    System.out.printf("Sum = %20.16f%n", sum2 - Math.PI * Math.PI / 6);

    // 4.5.3
    final DoubleStream doubleStream3 = IntStream.range(1, range).parallel().mapToDouble((x) -> 1.0 / x / x);
    final double[] doublesList = doubleStream3.toArray();
    double sum3 = 0;
    for (double aDoublesList : doublesList) {
      sum3 += aDoublesList;
    }
    System.out.printf("Sum = %20.16f%n", sum2 - Math.PI * Math.PI / 6);

    // 4.5.4
    DoubleSupplier doubleSupplier = new DoubleSupplier() {
      private double current = 0.0;

      public double getAsDouble() {
        final double next = this.current + 1.0;
        this.current = next;
        return 1 / next / next;
      }
    };

    final DoubleStream doubleStream4 = DoubleStream.generate(doubleSupplier).limit(range);
    final double sum4 = doubleStream4.sum();
    System.out.printf("Sum = %20.16f%n", sum4 - Math.PI * Math.PI / 6);

    // 4.5.5
    final DoubleStream doubleStream5 = DoubleStream.generate(doubleSupplier).limit(range);
    final double sum5 = doubleStream5.parallel().sum();
    System.out.printf("Sum = %20.16f%n", sum5 - Math.PI * Math.PI / 6);
  }

  public static Stream<String> readWords(String filename) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      return reader.lines();
    } catch (IOException exn) { 
      return Stream.<String>empty();
    }
  }

  public static boolean isPalindrome(String s) {
    Iterator<Integer> i1 = s.chars().boxed().collect(Collectors.toCollection(ArrayDeque::new)).descendingIterator();
    Iterator<Integer> i2 = s.chars().boxed().iterator();
    while(i1.hasNext() && i2.hasNext())
      if (!i1.next().equals(i2.next())) return false;
    return true;
  }

  public static Map<Character,Integer> letters(String s) {
    Map<Character,Integer> res = new TreeMap<>();
    // TO DO: Implement properly
    return res;
  }
}
