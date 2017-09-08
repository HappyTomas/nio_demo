package me.zhongmingmao.nio.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioServer {
    
    private static final int BUFFER_SIZE = 1024;
    private static final int PORT = 9000;
    
    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                Socket socket = serverSocket.accept(); // 阻塞
                pool.submit(() -> { // 接收一个连接后，创建线程进行处理
                    try (InputStream inputStream = socket.getInputStream()) {
                        int readBytes = -1;
                        while ((readBytes = inputStream.read(buffer)) != -1) {
                            byte[] receivedBytes = Arrays.copyOf(buffer, readBytes);
                            System.out.println(String.format("Receive Msg From %s , %s",
                                    socket.getRemoteSocketAddress(), new String(receivedBytes)));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}