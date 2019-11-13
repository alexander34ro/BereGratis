package CAS;// For week 10
// sestoft@itu.dk * 2014-11-05, 2015-10-14
import java.util.concurrent.CyclicBarrier;

interface Histogram {
    void increment(int bin);

    int getCount(int bin);

    int getSpan();

    int[] getBins();

    int getAndClear(int bin);

    void transferBins(Histogram hist);
}

class TestStmHistogram {
    public static void main(String[] args) {

        Timer t = new Timer();
        Histogram total=new CasHistogram(30);
        CasHistogram primes = countPrimeFactorsWithStmHistogram();
        //dump(primes);
        Thread thread=new Thread(()->{
            for(int i=0;i<200;i++) {

                try {

                    total.transferBins(primes);
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            dump(primes);
            System.out.println("--------");

            dump(total);
        });

        thread.start();
        try {
            thread.join();
            double time = t.check() * 1e6;
            System.out.printf("time = %10.2f us;", time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static CasHistogram countPrimeFactorsWithStmHistogram() {
        final CasHistogram histogram = new CasHistogram(30);
        final int range = 4_000_000;
        final int threadCount = 10, perThread = range / threadCount;
        final CyclicBarrier startBarrier = new CyclicBarrier(threadCount + 1),
                stopBarrier = startBarrier;
        final Thread[] threads = new Thread[threadCount];
        for (int t = 0; t < threadCount; t++) {
            final int from = perThread * t,
                    to = (t + 1 == threadCount) ? range : perThread * (t + 1);
            threads[t] =
                    new Thread(() -> {
                        try {
                            startBarrier.await();
                        } catch (Exception exn) {
                        }
                        for (int p = from; p < to; p++)
                            histogram.increment(countFactors(p));
                        System.out.print("*");
                        try {
                            stopBarrier.await();
                        } catch (Exception exn) {
                        }
                    });
            threads[t].start();
        }
        try {
            startBarrier.await();
        } catch (Exception exn) {
        }
        try {
            stopBarrier.await();
        } catch (Exception exn) {
        }
        //  dump(histogram);
        return histogram;
    }

    public static void dump(Histogram histogram) {
        int totalCount = 0;
        for (int bin = 0; bin < histogram.getSpan(); bin++) {
            System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
            totalCount += histogram.getCount(bin);
        }
        System.out.printf("      %9d%n", totalCount);
    }

    public static int countFactors(int p) {
        if (p < 2)
            return 0;
        int factorCount = 1, k = 2;
        while (p >= k * k) {
            if (p % k == 0) {
                factorCount++;
                p /= k;
            } else
                k++;
        }
        return factorCount;
    }
}
