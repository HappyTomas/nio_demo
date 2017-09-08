package me.zhongmingmao.nio.scatter_gather;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class ScattingAndGather {
    
    private static final int BUFFER_SIZE = 8;
    private static final String FILE_NAME = "file/ScattingAndGather.txt";
    
    public static void main(String[] args) {
        gather();
        scatter();
        /*
        输出：
        java.nio.HeapByteBuffer[pos=0 lim=8 cap=8] : headerbo
        java.nio.HeapByteBuffer[pos=0 lim=2 cap=8] : dy
         */
    }
    
    private static void gather() {
        ByteBuffer header = ByteBuffer.allocate(BUFFER_SIZE);
        ByteBuffer body = ByteBuffer.allocate(BUFFER_SIZE);
        
        header.put("header".getBytes());
        body.put("body".getBytes());
        ByteBuffer[] buffers = {header, body};
        
        try (RandomAccessFile outputStream = new RandomAccessFile(FILE_NAME, "rw");
             FileChannel channel = outputStream.getChannel()) {
            header.flip();
            body.flip();
            channel.write(buffers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void scatter() {
        
        ByteBuffer header = ByteBuffer.allocate(BUFFER_SIZE);
        ByteBuffer body = ByteBuffer.allocate(BUFFER_SIZE);
        
        ByteBuffer[] buffers = {header, body};
        
        try (RandomAccessFile outputStream = new RandomAccessFile(FILE_NAME, "rw");
             FileChannel channel = outputStream.getChannel()) {
            while (channel.read(buffers) != -1) {
                Arrays.stream(buffers).forEach(buffer -> {
                    buffer.flip();
                    System.out.print(buffer + " : ");
                    while (buffer.hasRemaining()) {
                        System.out.print((char) buffer.get());
                    }
                    buffer.compact();
                    System.out.println();
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}