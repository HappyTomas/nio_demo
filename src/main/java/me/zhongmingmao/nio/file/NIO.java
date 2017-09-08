package me.zhongmingmao.nio.file;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIO {
    
    private static final int BUFFER_SIZE = 1024;
    
    public static void main(String[] args) throws IOException {
        RandomAccessFile file = new RandomAccessFile("file/nio.txt", "rw");
        try (FileChannel channel = file.getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            while (channel.read(buffer) != -1) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    System.out.print((char) buffer.get());
                }
                buffer.compact();
            }
        }
    }
}