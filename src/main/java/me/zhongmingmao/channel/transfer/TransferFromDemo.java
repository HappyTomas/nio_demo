package me.zhongmingmao.channel.transfer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class TransferFromDemo {
    
    public static void main(String[] args) {
        try (RandomAccessFile fromFile = new RandomAccessFile("/tmp/fromFile.txt", "rw");
             FileChannel fromChannel = fromFile.getChannel();
             RandomAccessFile toFile = new RandomAccessFile("/tmp/toFile.txt", "rw");
             FileChannel toChannel = toFile.getChannel()) {
            toChannel.transferFrom(fromChannel, 0, fromChannel.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
