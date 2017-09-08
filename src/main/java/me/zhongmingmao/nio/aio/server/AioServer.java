package me.zhongmingmao.nio.aio.server;

public class AioServer {
    
    private static int PORT = 9000;
    
    public static void main(String[] args) {
        new Thread(new AsyncServerHandler(PORT)).start();
    }
}