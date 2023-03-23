package io.trabe.monitoringdemo;


import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Slf4j
public class DemoController {

    private final Counter counter;

    private AtomicInteger testingGauge;

    private Random random;

    private MyQueue myQueue;

    private MeterRegistry meterRegistry;

    private Timer timer;


    public DemoController(MeterRegistry meterRegistry, MyQueue myQueue) {
        this.meterRegistry = meterRegistry;
        this.myQueue = myQueue;
        this.random = new Random();


        counter = Counter.builder("testing.counter")
                .description("A testing counter example")
                .baseUnit("galliElephants")
                .tags("aTag", "aTagValue")
                .register(meterRegistry);


//        testingGauge = meterRegistry.gauge("testing.gauge", new AtomicInteger(0));

//        Gauge gauge = Gauge.builder("testing.gauge", myQueue, MyQueue::size)
//                .tags("aTag", "aTagValue")
//                .description("description")
//                .register(meterRegistry);

        timer = Timer.builder("testing.timer")
                .description("A testing timer example")
                .tag("aTag", "aTagValue")
                .publishPercentileHistogram(false)
                .register(meterRegistry);
    }


    @GetMapping("/test-counter")
    public void testCounter() {

//        meterRegistry.counter("testing.counter", "aTag", "aTagValue")
//                .increment(random.nextInt(9));

        counter.increment(random.nextInt(9));

        log.info("test-counter called");
    }


    @GetMapping("/test-gauge")
    public void testGauge() throws InterruptedException {

        int randomInt = random.nextInt(9);

        if ((randomInt % 2) == 0) {
            myQueue.add(randomInt);
        } else {
            myQueue.remove(randomInt);
        }

        log.info("test-gauge. Queue size:{} ", myQueue.size());

//        testingGauge.set(myQueue.size());

    }


    @GetMapping("/test-timer")
    public void testTimer() {

        Timer.Sample sample = Timer.start(meterRegistry);

//        timer.record(() -> {
        try {
            randomPauseAndException();

        } catch (Exception e) {
            sample.stop(meterRegistry.timer("testing.timer", "exception", e.getClass().getName()));
        }
//    }
        sample.stop(timer);
    }


    @Timed(value = "testing.timed", description = "Time taken to execute test operation")
    @GetMapping("/test-timed")
    public void testTimed() throws MyException {
        randomPauseAndException();
    }


    @Timed(value = "testing.longtask", longTask = true, description = "Test longtask timer")
    @GetMapping("/test-longtask")
    public void testLongtaskTimer() {
        int pause = 3 * (random.nextInt(9) + 1);
        doPause(pause);
    }

    private static void doPause(int pause) {
        try {
            log.info("pause is {}", pause);
            Thread.sleep(pause * 500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void randomPauseAndException() throws MyException {
        int pause = random.nextInt(9) + 1;

        doPause(pause);

        if (pause % 9 == 0) throw new MyException();

        if (pause % 8 == 0) throw new MyRtException();
    }

    public class MyRtException extends RuntimeException {}
    public class MyException extends Exception {}

}
