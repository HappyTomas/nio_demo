package me.zhongmingmao.nio.aio.client;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class AioClient {
    
    private static int PORT = 9000;
    private static int THREAD_COUNT = 1;
    
    public static void main(String[] args) throws IOException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        IntStream.range(0, THREAD_COUNT).forEach(value -> pool.submit(new AsyncClientHandler(PORT)));
        pool.shutdown();
        TimeUnit.DAYS.sleep(1);
    }
}