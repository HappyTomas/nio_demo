package me.zhongmingmao.channel.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

public abstract class AbstractNioComponent implements Runnable {
    
    protected static final int BUFFER_SIZE = 1024;
    protected static final int PORT = 9000;
    protected static final int TIMEOUT = 3000;
    
    private String name;
    
    public AbstractNioComponent(String name) {
        this.name = name;
    }
    
    protected void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        // 注册事件
        socketChannel.register(key.selector(), SelectionKey.OP_READ | SelectionKey.OP_WRITE,
                ByteBuffer.allocateDirect(BUFFER_SIZE));
    }
    
    protected void handleConnect(SelectionKey key) {
        System.out.println("isConnectable = true");
    }
    
    protected void handleRead(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        int bytesRead;
        while ((bytesRead = socketChannel.read(buffer)) > 0) {
            buffer.flip();
            System.out.print(String.format("[%s] Receive Msg From %s to %s , ", name,
                    ((InetSocketAddress) socketChannel.getRemoteAddress()).getPort(),
                    ((InetSocketAddress) socketChannel.getLocalAddress()).getPort()));
            while (buffer.hasRemaining()) {
                System.out.print((char) buffer.get());
            }
            buffer.clear();
            System.out.println();
        }
        if (bytesRead == -1) {
            socketChannel.close();
        }
    }
    
    protected void handleWrite(SelectionKey key) throws IOException, InterruptedException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        
        System.out.println(String.format("[%s] Send Msg From %s to %s , %s", name,
                ((InetSocketAddress) socketChannel.getLocalAddress()).getPort(),
                ((InetSocketAddress) socketChannel.getRemoteAddress()).getPort(), name));
        buffer.put(name.getBytes());
        
        buffer.flip();
        while (buffer.hasRemaining()) {
            socketChannel.write(buffer);
        }
        buffer.clear();
        TimeUnit.SECONDS.sleep(1);
    }
}