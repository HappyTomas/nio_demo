package me.zhongmingmao.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

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
            socketChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE,
                    ByteBuffer.allocateDirect(BUFFER_SIZE));
            handle(selector);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}