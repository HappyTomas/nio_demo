package me.zhongmingmao.nio.aio.client;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

@AllArgsConstructor
public class ClientWriteHandler implements CompletionHandler<Integer, ByteBuffer> {
    
    private static final int BUFFER_SIZE = 1024;
    private AsynchronousSocketChannel clientChannel;
    
    @Override
    public void completed(Integer result, ByteBuffer byteBuffer) {
        if (byteBuffer.hasRemaining()) {
            // 完成所有数据的写入
            clientChannel.write(byteBuffer, byteBuffer, this);
        } else {
            ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            // 异步读
            clientChannel.read(readBuffer, readBuffer, new ClientReadHandler(clientChannel));
        }
    }
    
    @Override
    public void failed(Throwable exc, ByteBuffer byteBuffer) {
        System.err.println("AioClient ClientWriteHandler Error...");
        try {
            clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}