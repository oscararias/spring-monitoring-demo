package io.trabe.monitoringdemo;

import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
public class MyQueue {

    private BlockingQueue<String> myQueue = new ArrayBlockingQueue<>(100000);

    public void add(int elements) {
        for (int i = 0; i < elements; i++) {
                myQueue.add("A");
        }
    }

    public void remove(int elements) throws InterruptedException {

        for (int i = 0; i < elements; i++) {
            myQueue.take();
        }
    }

    public int size() {
        return myQueue.size();
    }

}
