package io.github.yuricaprini.unit;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import io.github.yuricaprini.wordleprotocol.ProtocolFactoryProvider;
import io.github.yuricaprini.wordleprotocol.ioutils.OutputQueue;

public class OutputQueueTest {

    private OutputQueue outputQueue;

    @BeforeEach
    public void setUp() {
        outputQueue = ProtocolFactoryProvider.newOutputQueueFactory().createOutputQueue();
    }

    @Test
    void enqueueShouldThrowExceptionIfDataNull() {
        assertThrows(NullPointerException.class, () -> {
            outputQueue.enqueue(null);
        });
    }

    @Test
    void enqueueShouldNotAddEmptyDataToQueue() {
        assertFalse(outputQueue.enqueue(new byte[0]));
    }

    @Test
    void enqueueShouldWorkProperly() {
        assertTrue(outputQueue.enqueue("Test data".getBytes()));
        assertFalse(outputQueue.isEmpty());
    }

    @Test
    void drainToShouldThrowExceptionIfChannelNull() {
        assertThrows(NullPointerException.class, () -> {
            outputQueue.drainTo(null);
        });
    }

    @Test
    void drainToShouldwriteLessBytesIfChannelWriteAttemptOfWritingNBytesFail() throws IOException {
        byte[] testData = "This is a test.".getBytes();
        int expectedWrittenBytes = 3;
        outputQueue.enqueue(testData);

        WritableByteChannel channel = Mockito.mock(WritableByteChannel.class);
        Mockito.when(channel.write(Mockito.any(ByteBuffer.class))).thenAnswer(invocation -> {
            ByteBuffer buffer = (ByteBuffer) invocation.getArguments()[0];
            for (int i = 0; i < expectedWrittenBytes; i++) {
                buffer.get();
            }
            return expectedWrittenBytes;
        });

        int totalWrittenBytes = outputQueue.drainTo(channel);

        assertEquals(expectedWrittenBytes, totalWrittenBytes);
        assertFalse(outputQueue.isEmpty());
    }

    @Test
    void drainToShouldWorkProperly() throws IOException {
        byte[] testData = "This is a test.".getBytes();

        outputQueue.enqueue(testData);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        WritableByteChannel channel = Channels.newChannel(outputStream);

        int totalWrittenBytes = outputQueue.drainTo(channel);

        assertEquals(testData.length, totalWrittenBytes);
        assertTrue(outputQueue.isEmpty());
    }

    @Test
    void flowToShouldWorkProperly() throws IOException {
        byte[] thisIs = "This is ".getBytes();
        byte[] a = "a".getBytes();
        byte[] test = " test.".getBytes();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        WritableByteChannel channel = Channels.newChannel(outputStream);

        int totalWrittenBytes = 0;

        assertTrue(outputQueue.enqueue(thisIs));
        assertTrue(outputQueue.enqueue(a));
        totalWrittenBytes += outputQueue.drainTo(channel);
        assertTrue(outputQueue.enqueue(test));
        totalWrittenBytes += outputQueue.drainTo(channel);

        assertEquals(thisIs.length + a.length + test.length, totalWrittenBytes);
        assertTrue(outputQueue.isEmpty());
        assertArrayEquals("This is a test.".getBytes(), outputStream.toByteArray());
    }
}
