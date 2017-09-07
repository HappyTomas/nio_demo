package me.zhongmingmao.channel.scatter_gather;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ScattingAndGather {
    
    public static void main(String[] args) {
        ByteBuffer header = ByteBuffer.allocate(32);
        ByteBuffer body = ByteBuffer.allocate(32);
        
        header.put("header".getBytes());
        body.put("body".getBytes());
        
        ByteBuffer[] buffers = {header, body};
        try (RandomAccessFile file = new RandomAccessFile("/tmp/ScattingAndGather.txt", "rw");
             FileChannel channel = file.getChannel()) {
            channel.write(buffers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}