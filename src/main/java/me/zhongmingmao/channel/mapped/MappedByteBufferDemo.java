package me.zhongmingmao.channel.mapped;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.time.Duration;
import java.time.LocalDateTime;

public class MappedByteBufferDemo {
    
    public static void main(String[] args) {
        String fileName = "/tmp/hello.txt";
        long byteBufferRead = byteBufferRead(fileName);
        long mappedByteBufferRead = mappedByteBufferRead(fileName);
        
        System.out.println(String.format("byteBufferRead=%sns , mappedByteBufferRead=%sns , " +
                        "byteBufferRead/mappedByteBufferRead=%s",
                byteBufferRead, mappedByteBufferRead, byteBufferRead / mappedByteBufferRead));
        /*
        /tmp/hello.txt为100M时
        输出：
        byteBufferRead=427000000ns , mappedByteBufferRead=6000000ns , byteBufferRead/mappedByteBufferRead=71
        
        /tmp/hello.txt为500M时
        输出：
        byteBufferRead=3292000000ns , mappedByteBufferRead=3000000ns , byteBufferRead/mappedByteBufferRead=1097
        
        /tmp/hello.txt为800M时
        输出：
        byteBufferRead=4681000000ns , mappedByteBufferRead=3000000ns , byteBufferRead/mappedByteBufferRead=1560
         */
    }
    
    private static long byteBufferRead(final String fileName) {
        try (RandomAccessFile file = new RandomAccessFile(fileName, "r");
             FileChannel channel = file.getChannel()) {
            LocalDateTime start = LocalDateTime.now();
            ByteBuffer buffer = ByteBuffer.allocate((int) file.length());
            buffer.clear();
            channel.read(buffer);
            LocalDateTime end = LocalDateTime.now();
            return Duration.between(start, end).toNanos();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    private static long mappedByteBufferRead(final String fileName) {
        try (RandomAccessFile file = new RandomAccessFile(fileName, "r");
             FileChannel channel = file.getChannel()) {
            LocalDateTime start = LocalDateTime.now();
            // 被MappedByteBuffer打开的文件只有在垃圾回收时才会被关闭
            // MappedByteBuffer将文件直接映射到虚拟内存,用于处理大文件，读写性能极高
            MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            LocalDateTime end = LocalDateTime.now();
            return Duration.between(start, end).toNanos();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
