package nextstep.learning.http;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.setMaxElementsForPrinting;

class ThreadTest {

    @Test
    void countDownLatchTest() throws InterruptedException {
        int numberOfThreads = 3;
        CountDownLatch latch = new CountDownLatch(3);
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        MyCounter counter = new MyCounter();
        System.out.println(latch.getCount());
        latch.countDown();
        System.out.println(latch.getCount());
        latch.countDown();
        System.out.println(latch.getCount());

//        for (int i = 0; i < numberOfThreads; i++) {
//            service.execute(() -> {
//                counter.increment();
//                latch.countDown();
//            });
//        }
//        latch.await();
    }

    @Test
    void semaphoreTest() throws InterruptedException {
        Semaphore semaphore = new Semaphore(10);
        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            semaphore.acquire();
        }
        assertThat(semaphore.tryAcquire()).isFalse();
        assertThat(semaphore.tryAcquire(0)).isTrue();
        assertThat(semaphore.tryAcquire(5)).isFalse();
        semaphore.release(10);
        assertThat(semaphore.tryAcquire(10)).isTrue();
    }

    @Test
    void cyclicBarrierTest() throws InterruptedException, BrokenBarrierException {
        CyclicBarrier barrier = new CyclicBarrier(5);
        ExecutorService service = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5 ; i++) {
            service.execute(() -> {
                try {
                    barrier.await();
                    System.out.println("bepoz");
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
        Thread.sleep(100);
        System.out.println("a");
        barrier.await();
        barrier.await();
        barrier.await();
        barrier.await();
        barrier.await();
        System.out.println("b");
    }


    @Test
    void testCounterWithConcurrency() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        MyCounter counter = new MyCounter();
        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                counter.increment();
                latch.countDown();
            });
        }
        latch.await();
        assertThat(counter.getCount()).isEqualTo(numberOfThreads);
    }

    @Test
    void testSummationWithConcurrency() throws InterruptedException {
        int numberOfThreads = 2;
        ExecutorService service = Executors.newFixedThreadPool(1);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        MyCounter counter = new MyCounter();
        for (int i = 0; i < numberOfThreads; i++) {
            service.submit(() -> {
                counter.increment();
                latch.countDown();
            });
        }
        latch.await();
        assertThat(counter.getCount()).isEqualTo(numberOfThreads);
    }

    static class MyCounter {

        private int count;

        public void increment() {
            int temp = count;
            count = temp + 1;
        }

        public int getCount() {
            return count;
        }
    }
}
