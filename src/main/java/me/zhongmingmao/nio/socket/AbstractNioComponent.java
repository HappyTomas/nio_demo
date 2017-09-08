package me.zhongmingmao.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public abstract class AbstractNioComponent implements Runnable {
    
    protected static final int BUFFER_SIZE = 1024;
    protected static final int PORT = 9000;
    protected static final int TIMEOUT = 3000;
    
    private String name;
    
    public AbstractNioComponent(String name) {
        this.name = name;
    }
    
    protected void handle(Selector selector) throws IOException, InterruptedException {
        while (true) {
            // 从上次调用select方法后有多少Channel变成就绪状态
            if (selector.select(TIMEOUT) == 0) {
                System.out.println("Waiting...");
                continue;
            }
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                handle(key);
                // selector不会自动清除SelectionKey实例，因此必须在处理完Channel后手动移除SelectionKey实例
                // 以便在下次Channel变成就绪状态时，selector会再次将SelectionKey实例放入Set<SelectionKey>中
                iterator.remove();
            }
        }
    }
    
    private void handle(SelectionKey key) throws InterruptedException {
        if (null == key || !key.isValid()) {
            return;
        }
        try {
            if (key.isAcceptable()) {
                handleAccept(key);
            }
            if (key.isConnectable()) {
                handleConnect(key);
            }
            if (key.isReadable()) {
                handleRead(key);
            }
            if (key.isWritable()) {
                handleWrite(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
            key.cancel();
        }
        
    }
    
    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        // 注册事件
        socketChannel.register(key.selector(), SelectionKey.OP_READ | SelectionKey.OP_WRITE,
                ByteBuffer.allocateDirect(BUFFER_SIZE));
        System.out.println(String.format("Accept New SocketChannel : %s",
                ((InetSocketAddress) socketChannel.getRemoteAddress()).getPort()));
    }
    
    private void handleConnect(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        if (!channel.finishConnect()) {
            System.exit(-1);
        }
        System.out.println("Connectted!!");
    }
    
    private void handleRead(SelectionKey key) throws IOException {
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
    
    private void handleWrite(SelectionKey key) throws IOException, InterruptedException {
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