// For week 2
// sestoft@itu.dk * 2014-09-04
// thdy@itu.dk * 2019

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.LongAdder;

interface Histogram {
    void increment(int bin);

    int getCount(int bin);

    int getSpan();

    int[] getBins();
}

abstract class SimpleHistogram {
    public static void main(String[] args) {
        /*
        final Histogram histogram = new Histogram1(30);
        histogram.increment(7);
        histogram.increment(13);
        histogram.increment(7);
        dump(histogram);
        */

        final int range = 5_000_000;
        //final Histogram histogram = new Histogram2(30);
        //final Histogram histogram = new Histogram3(30);
        //final Histogram histogram = new Histogram4(30);
        final Histogram histogram = new Histogram5(30);
        for (int i = 0; i < range; i++) {
            final int j = i;
            new Thread(() -> {
                int nr_factors = countPrimeFactors(j);
                histogram.increment(nr_factors);
            }).start();
        }
        dump(histogram);
    }

    public static void dump(Histogram histogram) {
        int totalCount = 0;
        for (int bin = 0; bin < histogram.getSpan(); bin++) {
            System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
            totalCount += histogram.getCount(bin);
        }
        System.out.printf("      %9d%n", totalCount);
    }

    public static int countPrimeFactors(int p) {
        if (p < 2)
            return 0;
        int factorCount = 1, k = 2;
        while (p >= k * k) {
            if (p % k == 0) {
                factorCount++;
                p /= k;
            } else {
                k++;
            }
        }
        return factorCount;
    }
}

class Histogram1 implements Histogram {
    private int[] counts;

    public Histogram1(int span) {
        this.counts = new int[span];
    }

    public void increment(int bin) {
        counts[bin] = counts[bin] + 1;
    }

    public int getCount(int bin) {
        return counts[bin];
    }

    public int getSpan() {
        return counts.length;
    }

    @Override
    public int[] getBins() {
        return new int[0];
    }
}

class Histogram2 implements Histogram {
    private int[] counts;

    public Histogram2(int span) {
        this.counts = new int[span];
    }

    public synchronized void increment(int bin) {
        counts[bin] = counts[bin] + 1;
    }

    public synchronized int getCount(int bin) {
        return counts[bin];
    }

    public int getSpan() {
        return counts.length;
    }

    @Override
    public int[] getBins() {
        int[] bins = new int[getSpan()];
        synchronized (this) {
            for (int i = 0; i < getSpan(); i++) {
                bins[i] = getCount(i);
            }
        }
        return bins;
    }
}

class Histogram3 implements Histogram {
    private AtomicInteger[] counts;

    public Histogram3(int span) {
        this.counts = new AtomicInteger[span];
        for (int i = 0; i < span; i++) {
            this.counts[i] = new AtomicInteger(0);
        }
    }

    public void increment(int bin) {
        counts[bin].incrementAndGet();
    }

    public int getCount(int bin) {
        return counts[bin].get();
    }

    public int getSpan() {
        return counts.length;
    }

    @Override
    public int[] getBins() {
        int[] bins = new int[getSpan()];
        synchronized (this) {
            for (int i = 0; i < getSpan(); i++) {
                bins[i] = getCount(i);
            }
        }
        return bins;
    }
}

class Histogram4 implements Histogram {
    private AtomicIntegerArray counts;

    public Histogram4(int span) {
        this.counts = new AtomicIntegerArray(span);
    }

    public void increment(int bin) {
        counts.incrementAndGet(bin);
    }

    public int getCount(int bin) {
        return counts.get(bin);
    }

    public int getSpan() {
        return counts.length();
    }

    @Override
    public int[] getBins() {
        int[] bins = new int[getSpan()];
        synchronized (this) {
            for (int i = 0; i < getSpan(); i++) {
                bins[i] = getCount(i);
            }
        }
        return bins;
    }
}

class Histogram5 implements Histogram {
    private LongAdder[] counts;

    public Histogram5(int span) {
        this.counts = new LongAdder[span];
        for (int i = 0; i < span; i++) {
            this.counts[i] = new LongAdder();
        }
    }

    public void increment(int bin) {
        counts[bin].increment();
    }

    public int getCount(int bin) {
        return counts[bin].intValue();
    }

    public int getSpan() {
        return counts.length;
    }

    @Override
    public int[] getBins() {
        return new int[0];
    }
}
