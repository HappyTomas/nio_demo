package me.zhongmingmao.nio.aio.server;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

@AllArgsConstructor
public class ServerWriteHandler implements CompletionHandler<Integer, ByteBuffer> {
    
    private static final int BUFFER_SIZE = 1024;
    private AsynchronousSocketChannel clientChannel;
    
    @Override
    public void completed(Integer result, ByteBuffer byteBuffer) {
        if (byteBuffer.hasRemaining()) {
            // 完成所有数据的写入
            clientChannel.write(byteBuffer, byteBuffer, this);
        } else {
            ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            // 完成异步写后进行异步读（读取客户端发送的信息）
            clientChannel.read(readBuffer, readBuffer, new ServerReadHandler(clientChannel));
        }
    }
    
    @Override
    public void failed(Throwable exc, ByteBuffer byteBuffer) {
        System.err.println("AioServer ServerWriteHandler Error!!");
        try {
            clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}