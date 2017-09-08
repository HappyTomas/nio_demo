package me.zhongmingmao.nio.aio.client;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

@AllArgsConstructor
public class ClientConnectHandler implements CompletionHandler<Void, AsyncClientHandler> {
    
    private AsynchronousSocketChannel clientChannel;
    
    /**
     * 客户端成功连接到服务器
     */
    @Override
    public void completed(Void result, AsyncClientHandler clientHandler) {
        System.out.println("AioClient Connect To AioServer Successfully!!");
    }
    
    /**
     * 客户端连接服务器失败
     */
    @Override
    public void failed(Throwable exc, AsyncClientHandler clientHandler) {
        System.err.println("AioClient Fail To Connect To AioServer!!");
        exc.printStackTrace();
        try {
            clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}