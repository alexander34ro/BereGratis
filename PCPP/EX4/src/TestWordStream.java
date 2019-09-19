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
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestWordStream {
  public static void main(String[] args) {
    String filename = "/usr/share/dict/words";
    System.out.println(readWords(filename).count());

    // *************
    // DoubleStreams
    // 4.5.1
    final DoubleStream doubleStream1 = IntStream.range(1, 1_000_000_000).mapToDouble((x) -> 1.0 / x / x);
    final double sum1 = doubleStream1.sum();
    System.out.printf("Sum = %20.16f%n", sum1 - Math.PI * Math.PI / 6);

    // 4.5.2
    final DoubleStream doubleStream2 = IntStream.range(1, 1_000_000_000).parallel().mapToDouble((x) -> 1.0 / x / x);
    final double sum2 = doubleStream2.parallel().sum();
    System.out.printf("Sum = %20.16f%n", sum2 - Math.PI * Math.PI / 6);

    // 4.5.3
    final DoubleStream doubleStream3 = IntStream.range(1, 1_000_000_000).parallel().mapToDouble((x) -> 1.0 / x / x);
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

    final DoubleStream doubleStream4 = DoubleStream.generate(doubleSupplier).limit(1_000_000_000);
    final double sum4 = doubleStream4.sum();
    System.out.printf("Sum = %20.16f%n", sum4 - Math.PI * Math.PI / 6);

    // 4.5.5
    final DoubleStream doubleStream5 = DoubleStream.generate(doubleSupplier).limit(1_000_000_000);
    final double sum5 = doubleStream5.parallel().sum();
    System.out.printf("Sum = %20.16f%n", sum5 - Math.PI * Math.PI / 6);
  }

  public static Stream<String> readWords(String filename) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      // TO DO: Implement properly
      return Stream.<String>empty(); 
    } catch (IOException exn) { 
      return Stream.<String>empty();
    }
  }

  public static boolean isPalindrome(String s) {
    // TO DO: Implement properly
    return false; 
  }

  public static Map<Character,Integer> letters(String s) {
    Map<Character,Integer> res = new TreeMap<>();
    // TO DO: Implement properly
    return res;
  }
}
