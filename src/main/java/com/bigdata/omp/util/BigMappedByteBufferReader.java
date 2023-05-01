package com.bigdata.omp.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class BigMappedByteBufferReader {
    private MappedByteBuffer[] mappedByteBuffers;
    private FileInputStream inputStream;
    private FileChannel fileChannel;

    private int bufferCountIndex = 0;
    private int bufferCount;

    private int byteBufferSize;
    private byte[] byteBuffer;

    public BigMappedByteBufferReader(String fileName, int byteBufferSize) throws IOException {
        this.inputStream = new FileInputStream(fileName);
        this.fileChannel = inputStream.getChannel();
        long fileSize = fileChannel.size();
        this.bufferCount = (int) Math.ceil((double) fileSize / (double) Integer.MAX_VALUE);
        this.mappedByteBuffers = new MappedByteBuffer[bufferCount];
        this.byteBufferSize = byteBufferSize;

        long preLength = 0;
        long regionSize = Integer.MAX_VALUE;
        for (int i = 0; i < bufferCount; i++) {
            if (fileSize - preLength < Integer.MAX_VALUE) {
                regionSize = fileSize - preLength;
            }
            mappedByteBuffers[i] = fileChannel.map(FileChannel.MapMode.READ_ONLY, preLength, regionSize);
            preLength += regionSize;
        }
    }

    public synchronized int read() {
        if (bufferCountIndex >= bufferCount) {
            return -1;
        }

        int limit = mappedByteBuffers[bufferCountIndex].limit();
        int position = mappedByteBuffers[bufferCountIndex].position();

        int realSize = byteBufferSize;
        if (limit - position < byteBufferSize) {
            realSize = limit - position;
        }
        byteBuffer = new byte[realSize];
        mappedByteBuffers[bufferCountIndex].get(byteBuffer);

        //current fragment is end, goto next fragment start.
        if (realSize < byteBufferSize && bufferCountIndex < bufferCount) {
            bufferCountIndex++;
        }
        return realSize;
    }

    public void close() throws IOException {
        fileChannel.close();
        inputStream.close();
        for (MappedByteBuffer byteBuffer : mappedByteBuffers) {
            byteBuffer.clear();
        }
        byteBuffer = null;
    }

    public synchronized byte[] getCurrentBytes() {
        return byteBuffer;
    }
}

/**
 * 这段代码定义了一个名为 BigMappedByteBufferReader 的类构造函数，它接受一个文件名和一个缓冲区大小作为参数。它初始化了几个实例变量，
 * 包括 inputStream、fileChannel、bufferCount、mappedByteBuffers 和 byteBufferSize。
 * <p>
 * 构造函数首先使用提供的文件名创建了一个新的 FileInputStream，然后使用 FileInputStream 对象的 getChannel() 方法创建了一个 FileChannel。
 * 然后它使用 FileChannel 对象的 size() 方法确定了文件的大小，并使用整数除法和 Math 类的 ceil() 方法计算出需要多少个缓冲区来容纳文件数据。
 * <p>
 * 接下来，构造函数创建了一个 MappedByteBuffer 对象数组，其长度等于缓冲区计数。它还使用提供的缓冲区大小初始化了 byteBufferSize 实例变量。
 * <p>
 * 最后，构造函数进入一个循环，将文件的区域映射到每个缓冲区。循环迭代 bufferCount 次，在偏移量 preLength 处开始映射一个大小为 Integer.MAX_VALUE
 * 的区域（或者如果文件剩余的字节数小于 Integer.MAX_VALUE，则映射剩余的大小）到每个缓冲区中。mappedByteBuffers 数组用映射后的 MappedByteBuffer 对象填充，
 * preLength 被更新为指向下一个要映射的区域的起始位置。
 * <p>
 * MappedByteBuffer 是 Java NIO（New IO）库提供的一种缓冲区，用于在内存中创建一个映射文件的缓冲区，使得可以像操作内存一样操作文件，
 * 提高了 I/O 操作的效率。这种缓冲区与普通的 ByteBuffer 对象不同，它是通过操作系统提供的文件映射机制来创建的。
 * <p>
 * 具体来说，当使用 MappedByteBuffer 时，操作系统会将一个文件的部分或全部映射到内存中，而该缓冲区就是这个映射文件的一部分。
 * 因此，对 MappedByteBuffer 的读写操作实际上就是对映射文件的读写操作。由于数据是直接映射到内存中的，所以对 MappedByteBuffer 的读写操作可以达到很高的效率，
 * 特别是在读写大文件时。
 * <p>
 * 除了高效，MappedByteBuffer 还提供了一些其他的好处。例如，由于映射文件的一部分或全部已经加载到内存中，所以在读写文件时可以减少对磁盘的访问，
 * 从而减少了磁盘 I/O 操作的负载。此外，如果多个进程都使用了同一个映射文件，那么它们之间可以共享内存中的数据，这在一些特殊的应用场景下是非常有用的。
 * <p>
 * 如果文件过大，无法一次性映射到内存中，可以通过分片映射的方式来处理。具体来说，可以将文件划分为多个区域，然后分别创建 MappedByteBuffer 对象来映射这些区域。
 * 这样，每个 MappedByteBuffer 对象就只需要映射文件的一部分，就可以读写整个文件了。
 * <p>
 * 在代码中，可以参考构造函数中的实现方式，使用循环来映射文件的不同区域，然后将多个 MappedByteBuffer 对象组合起来形成一个大的缓冲区，再进行读写操作。
 * 在使用这种方法时，需要注意的是要保证每个区域的大小不超过 Integer.MAX_VALUE，否则会导致映射失败。
 * <p>
 * 此外，如果文件过大，也可以考虑使用其他的 I/O 操作方式，比如使用 BufferedInputStream 或 BufferedOutputStream 等带缓存的流进行读写操作，
 * 以减少对磁盘的访问次数，提高读写效率。
 */


/**
 * MappedByteBuffer 是 Java NIO（New IO）库提供的一种缓冲区，用于在内存中创建一个映射文件的缓冲区，使得可以像操作内存一样操作文件，
 * 提高了 I/O 操作的效率。这种缓冲区与普通的 ByteBuffer 对象不同，它是通过操作系统提供的文件映射机制来创建的。
 *
 * 具体来说，当使用 MappedByteBuffer 时，操作系统会将一个文件的部分或全部映射到内存中，而该缓冲区就是这个映射文件的一部分。
 * 因此，对 MappedByteBuffer 的读写操作实际上就是对映射文件的读写操作。由于数据是直接映射到内存中的，所以对 MappedByteBuffer 的读写操作可以达到很高的效率，
 * 特别是在读写大文件时。
 *
 * 除了高效，MappedByteBuffer 还提供了一些其他的好处。例如，由于映射文件的一部分或全部已经加载到内存中，所以在读写文件时可以减少对磁盘的访问，
 * 从而减少了磁盘 I/O 操作的负载。此外，如果多个进程都使用了同一个映射文件，那么它们之间可以共享内存中的数据，这在一些特殊的应用场景下是非常有用的。
 */

/**
 * 如果文件过大，无法一次性映射到内存中，可以通过分片映射的方式来处理。具体来说，可以将文件划分为多个区域，然后分别创建 MappedByteBuffer 对象来映射这些区域。
 * 这样，每个 MappedByteBuffer 对象就只需要映射文件的一部分，就可以读写整个文件了。
 *
 * 在代码中，可以参考构造函数中的实现方式，使用循环来映射文件的不同区域，然后将多个 MappedByteBuffer 对象组合起来形成一个大的缓冲区，再进行读写操作。
 * 在使用这种方法时，需要注意的是要保证每个区域的大小不超过 Integer.MAX_VALUE，否则会导致映射失败。
 *
 * 此外，如果文件过大，也可以考虑使用其他的 I/O 操作方式，比如使用 BufferedInputStream 或 BufferedOutputStream 等带缓存的流进行读写操作，
 * 以减少对磁盘的访问次数，提高读写效率。
 */