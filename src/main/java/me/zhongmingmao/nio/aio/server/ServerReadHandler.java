package me.zhongmingmao.nio.aio.server;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

@AllArgsConstructor
public class ServerReadHandler implements CompletionHandler<Integer, ByteBuffer> {
    
    private AsynchronousSocketChannel clientChannel;
    
    @Override
    public void completed(Integer result, ByteBuffer byteBuffer) {
        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        System.out.println(String.format("AioServer Receive %s", new String(bytes)));
        
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        
        // 源数据返回
        System.out.println(String.format("AioServer Send %s", new String(bytes)));
        // 完成异步读后进行异步写（响应客户端）
        clientChannel.write(writeBuffer, writeBuffer, new ServerWriteHandler(clientChannel));
    }
    
    @Override
    public void failed(Throwable exc, ByteBuffer byteBuffer) {
        System.err.println("AioServer ServerReadHandler Error!!");
        try {
            clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}