package me.zhongmingmao.nio.aio.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ServerAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncServerHandler> {
    
    private static final int BUFFER_SIZE = 1024;
    
    @Override
    public void completed(AsynchronousSocketChannel channel, AsyncServerHandler serverHandler) {
        try {
            System.out.println(String.format("AioServer Accept Successfully , Remote Address : %s",
                    channel.getRemoteAddress()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 继续接收其他客户端的请求
        serverHandler.accept(serverHandler, this);
        ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        // 接收到新的连接后进行异步读（读取客户端发送的信息）
        channel.read(readBuffer, readBuffer, new ServerReadHandler(channel));
    }
    
    @Override
    public void failed(Throwable exc, AsyncServerHandler serverHandler) {
        System.err.println("AioServer ServerAcceptHandler Error!!");
        exc.printStackTrace();
    }
}