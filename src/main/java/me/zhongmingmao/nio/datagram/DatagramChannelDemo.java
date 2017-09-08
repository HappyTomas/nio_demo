package me.zhongmingmao.nio.datagram;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DatagramChannelDemo {
    
    private static final int BUFFER_SIZE = 1024;
    private static final String HOSTNAME = "127.0.0.1";
    private static final int PORT = 1024;
    private static final int SLEEP_SECONDS = 1;
    private static final SocketAddress SOCKET_ADDRESS = new InetSocketAddress(HOSTNAME, PORT);
    
    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.submit(() -> {
            try (DatagramChannel datagramChannel = DatagramChannel.open()) {
                datagramChannel.socket().bind(SOCKET_ADDRESS);
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                while (true) {
                    TimeUnit.SECONDS.sleep(SLEEP_SECONDS);
                    datagramChannel.receive(buffer);
                    buffer.flip();
                    System.out.print("Receive ");
                    while (buffer.hasRemaining()) {
                        System.out.print((char) buffer.get());
                    }
                    System.out.println();
                    buffer.clear();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        pool.submit(() -> {
            try (DatagramChannel datagramChannel = DatagramChannel.open()) {
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                while (true) {
                    TimeUnit.SECONDS.sleep(SLEEP_SECONDS);
                    buffer.put(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).getBytes());
                    buffer.flip();
                    System.out.print("Send " + buffer);
                    datagramChannel.send(buffer, SOCKET_ADDRESS);
                    while (buffer.hasRemaining()) {
                        System.out.print((char) buffer.get());
                    }
                    System.out.println();
                    buffer.clear();
                }
                
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.DAYS);
    }
}