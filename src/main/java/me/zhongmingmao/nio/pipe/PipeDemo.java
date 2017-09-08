package me.zhongmingmao.nio.pipe;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PipeDemo {
    
    private static final int BUFFER_SIZE = 1024;
    private static final int THREAD_COUNT = 2;
    
    public static void main(String[] args) throws IOException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        Pipe pipe = Pipe.open();
        pool.submit(() -> {
            try (Pipe.SinkChannel sinkChannel = pipe.sink()) {// 写入数据
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                while (true) {
                    TimeUnit.SECONDS.sleep(1);
                    buffer.clear();
                    buffer.put(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).getBytes());
                    buffer.flip();
                    System.out.print("sinkChannel : ");
                    while (buffer.hasRemaining()) {
                        System.out.print(buffer);
                        sinkChannel.write(buffer);
                    }
                    System.out.println();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        });
        
        pool.submit(() -> {
            try (Pipe.SourceChannel sourceChannel = pipe.source()) {// 读取数据
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                while (true) {
                    TimeUnit.SECONDS.sleep(1);
                    buffer.clear();
                    while (sourceChannel.read(buffer) > 0) {
                        buffer.flip();
                        System.out.print("sourceChannel : ");
                        while (buffer.hasRemaining()) {
                            System.out.print((char) buffer.get());
                        }
                        System.out.println();
                    }
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        });
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.DAYS);
    }
}