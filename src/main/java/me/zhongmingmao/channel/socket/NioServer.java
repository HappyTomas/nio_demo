package me.zhongmingmao.channel.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

public class NioServer extends AbstractNioComponent {
    
    public static void main(String[] args) {
        new Thread(new NioServer(NioServer.class.getSimpleName())).start();
    }
    
    public NioServer(String name) {
        super(name);
    }
    
    @Override
    public void run() {
        try (Selector selector = Selector.open();
             ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            // 与Selector搭配使用，Channel必须处于非阻塞模式
            // FileChannel只能处于阻塞模式，不能与Selector一起使用
            serverSocketChannel.configureBlocking(false);
            // selector对serverSocketChannel上的OP_ACCEPT事件感兴趣
            // OP_ACCEPT : ServerSocketChannel准备好接收新进入的连接，接收就绪
            // OP_CONNECT : 某个Channel成功连接到另一个服务器，连接就绪
            // OP_READ : Channel有数据可读，读就绪
            // OP_WRITE : Channel等待写入数据，写就绪
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            
            while (true) {
                // 从上次调用select方法后有多少Channel变成就绪状态
                if (selector.select(TIMEOUT) == 0) {
                    System.out.println("Server Waiting...");
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        handleAccept(key);
                    }
                    if (key.isConnectable()) {
                        handleConnect(key);
                    }
                    if (key.isReadable()) {
                        handleRead(key);
                    }
                    if (key.isWritable() && key.isValid()) {
                        handleWrite(key);
                    }
                    // selector不会自动清除SelectionKey实例，因此必须在处理完Channel后手动移除SelectionKey实例
                    // 以便在下次Channel变成就绪状态时，selector会再次将SelectionKey实例放入Set<SelectionKey>中
                    iterator.remove();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}