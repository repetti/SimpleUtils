package org.repetti.utils.task;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * Date: 05/08/15
 */
public class FileCreate {
    public static void main(String[] args) throws IOException {
        final String filename = "/tmp/big.file";
        final long size = 60 * 1024 * 1024 * 1024L;
//        final long size = 640 * 1024 * 1024L;
        final int blockSize = 4096;
        final long updateThreshold = size / 100;
        long update = updateThreshold;
        long start = System.nanoTime();

        final byte[] block = new byte[blockSize];

        for (int i = 0; i < blockSize; i++) {
            block[i] = (byte) i;
        }

//        Path path = new UnixPath;
        File file = new File(filename);
//        Files.createFile(file)
        OutputStream stream = Files.newOutputStream(file.toPath(), StandardOpenOption.CREATE);

        for (long i = 0; i < size; i += blockSize) {
            if (i > update) {
                update += updateThreshold;
                final long now = System.nanoTime();
                System.out.println(i * 100 / size + "% in " + ((now - start) / 1000_000L) + " ms");
            }
            stream.write(block);
        }
        System.out.println("Complete in " + ((System.nanoTime() - start) / 1000_000L) + " ms");
        stream.close();
    }
}
