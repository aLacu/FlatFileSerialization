package it.lib.testModel;

import java.util.concurrent.atomic.AtomicInteger;

public class StatefulSupplier {

    private final AtomicInteger counter=new AtomicInteger(0);
    private final AtomicInteger internCounter=new AtomicInteger(0);

    public SampleObject supply(){
        int i1 = internCounter.incrementAndGet();
        int i2=counter.get();
        if (i1 >= i2) {
            i2=counter.incrementAndGet();
            internCounter.set(0);
        }
        final String s = String.valueOf(i2);
        return new SampleObject(s, i1);
    }
}
