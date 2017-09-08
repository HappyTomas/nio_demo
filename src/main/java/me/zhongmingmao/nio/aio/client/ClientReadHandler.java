package me.zhongmingmao.nio.aio.client;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

@AllArgsConstructor
public class ClientReadHandler implements CompletionHandler<Integer, ByteBuffer> {
    
    private AsynchronousSocketChannel clientChannel;
    
    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();
        byte[] bytes = new byte[attachment.remaining()];
        attachment.get(bytes);
        System.out.println(String.format("AioClient Receive %s", new String(bytes)));
    }
    
    @Override
    public void failed(Throwable exc, ByteBuffer byteBuffer) {
        System.err.println("AioClient ClientReadHandler Error...");
        try {
            clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}