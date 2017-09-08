package me.zhongmingmao.nio.aio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class AsyncClientHandler implements Runnable {
    
    private AsynchronousSocketChannel clientChannel;
    private int port;
    
    public AsyncClientHandler(final int port) {
        this.port = port;
        try {
            clientChannel = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        clientChannel.connect(new InetSocketAddress(port), null, new ClientConnectHandler(clientChannel));
        while (true) {
            String msg = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            System.out.println(String.format("AioServer Send %s", msg));
            ByteBuffer writeBuffer = ByteBuffer.allocate(msg.getBytes().length);
            writeBuffer.put(msg.getBytes());
            writeBuffer.flip();
            // 异步写
            clientChannel.write(writeBuffer, writeBuffer, new ClientWriteHandler(clientChannel));
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}