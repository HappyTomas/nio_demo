package me.zhongmingmao.channel.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class NioClient extends AbstractNioComponent {
    
    public static void main(String[] args) {
        new Thread(new NioClient(NioClient.class.getSimpleName())).start();
    }
    
    public NioClient(String name) {
        super(name);
    }
    
    @Override
    public void run() {
        try (Selector selector = Selector.open();
             SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(PORT));
            while (!socketChannel.finishConnect()) {
                TimeUnit.SECONDS.sleep(1);
            }
            
            socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE,
                    ByteBuffer.allocateDirect(BUFFER_SIZE));
            
            while (true) {
                if (selector.select(TIMEOUT) == 0) {
                    System.out.println("Client Waiting...");
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        handleRead(key);
                    }
                    if (key.isWritable() && key.isValid()) {
                        handleWrite(key);
                    }
                    iterator.remove();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    
}