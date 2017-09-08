package me.zhongmingmao.nio.file;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.IntStream;

public class BIO {
    
    private static final int BUFFER_SIZE = 1024;
    
    public static void main(String[] args) throws IOException {
        try (InputStream stream = new BufferedInputStream(new FileInputStream("file/bio.txt"))) {
            byte[] bytes = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = stream.read(bytes)) != -1) {
                IntStream.range(0, bytesRead).forEach(value -> System.out.print((char) bytes[value]));
            }
        }
    }
}