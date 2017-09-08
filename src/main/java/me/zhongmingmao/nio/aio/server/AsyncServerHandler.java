package me.zhongmingmao.nio.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

public class AsyncServerHandler implements Runnable {
    
    private AsynchronousServerSocketChannel serverChannel;
    
    public AsyncServerHandler(final int port) {
        try {
            serverChannel = AsynchronousServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(port));
            System.out.println(String.format("AioServer UP , Port : %s", port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        // 异步接收客户端连接
        serverChannel.accept(this, new ServerAcceptHandler());
        try {
            TimeUnit.DAYS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public <A> void accept(A attachment,
                           CompletionHandler<AsynchronousSocketChannel, ? super A> handler) {
        serverChannel.accept(attachment, handler);
    }
}