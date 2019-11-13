package CAS;

import java.util.concurrent.atomic.AtomicInteger;

class CasHistogram implements Histogram {

    private final AtomicInteger[] counts;

    CasHistogram(int span) {
        counts = new AtomicInteger[span];
        for (int i = 0; i < span; i++) counts[i] = new AtomicInteger(0);
    }


    @Override
    public void increment(int bin) {
        for (;;) {
            final int expected = counts[bin].get();
            final int increm = expected + 1;
            if (counts[bin].compareAndSet(expected, increm)) return;
        }
    }

    @Override
    public int getCount(int bin) {
        return counts[bin].get();
    }

    @Override
    public int getSpan() {
        return counts.length;
    }

    @Override
    public int[] getBins() {
        final int span = getSpan();
        final int[] bins = new int[span];
        for (int i = 0; i < span; i++) {
            bins[i] = counts[i].get();
        }
        return bins;
    }

    @Override
    public int getAndClear(int bin) {
        for (;;) {
            final int val = counts[bin].get();
            if (counts[bin].compareAndSet(val, 0)) return val;
        }
    }

    @Override
    public void transferBins(Histogram hist) {
        final int span = getSpan();
        for (int i = 0; i < span; i++) {
            final int val = hist.getAndClear(i);
            for (int j = 0; j < val; j++) increment(i);
        }
    }
}
