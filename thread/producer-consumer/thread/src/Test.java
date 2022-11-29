import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;


public class Test {
    //организуем очередь из 10 элементов
   private static BlockingQueue<Integer>  blockingQueue = new ArrayBlockingQueue<Integer>(10); // безопасна для потоков
    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    produce();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    consumer();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
            thread1.start();
            thread2.start();
            thread1.join();
            thread2.join();
    }
    private static void produce() throws InterruptedException {
        Random random = new Random();
        while (true){
            blockingQueue.put(random.nextInt(100 ));
        }
    }
    private static void consumer() throws InterruptedException {
        Random random = new Random();
        while (true){
            Thread.sleep(100);
                if (random.nextInt(10)==5)
            System.out.println(blockingQueue.take());
            System.out.println("размер очереди " + blockingQueue.size());
        }
    }
}

