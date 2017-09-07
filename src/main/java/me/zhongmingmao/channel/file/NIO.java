package me.zhongmingmao.channel.file;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIO {
    
    public static void main(String[] args) throws IOException {
        RandomAccessFile file = new RandomAccessFile("file/nio.txt", "rw");
        try (FileChannel channel = file.getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytesRead;
            while ((bytesRead = channel.read(buffer)) != -1) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    System.out.print((char) buffer.get());
                }
                buffer.compact();
            }
        }
    }
}