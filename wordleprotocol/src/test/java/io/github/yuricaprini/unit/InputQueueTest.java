package io.github.yuricaprini.unit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import io.github.yuricaprini.wordleprotocol.ProtocolFactoryProvider;
import io.github.yuricaprini.wordleprotocol.exceptions.EmptyInputQueueException;
import io.github.yuricaprini.wordleprotocol.ioutils.InputQueue;

public class InputQueueTest {

  @Test
  void constructorShouldThrowExceptionIfMaxSizeNegative() {
    assertThrows(IllegalArgumentException.class, () -> {
      ProtocolFactoryProvider.newInputQueueFactory().createInputQueue(-1);
    });
  }

  @Test
  void constructorShouldWorkProperly() {
    InputQueue iQ = ProtocolFactoryProvider.newInputQueueFactory().createInputQueue(42);
    assert (iQ.isEmpty());
  }

  @Test
  public void fillFromShouldThrowExceptionIfChannelNull() {
    InputQueue iQ = ProtocolFactoryProvider.newInputQueueFactory().createInputQueue(42);
    assertThrows(NullPointerException.class, () -> {
      iQ.fillFrom(null);
    });
  }

  @Test
  public void fillFromShouldWorkProperlyIfQueueSizeSmallerThanData() throws IOException {
    byte[] testData = "Test data".getBytes();
    int capacity = testData.length - 1;
    InputQueue iQ = ProtocolFactoryProvider.newInputQueueFactory().createInputQueue(capacity);
    ReadableByteChannel channel = Channels.newChannel(new ByteArrayInputStream(testData));

    int bytesRead = iQ.fillFrom(channel);

    assertEquals(capacity, bytesRead);
  }

  @Test
  public void dequeueShouldThrowExceptionIfQueueIsEmpty() {
    InputQueue iQ = ProtocolFactoryProvider.newInputQueueFactory().createInputQueue(42);
    assertThrows(EmptyInputQueueException.class, () -> {
      iQ.dequeue();
    });
  }

  @Test
  public void fillFromDoesNotIncrementSizeIfReadReturnMinusOne() throws IOException {
    InputQueue iQ = ProtocolFactoryProvider.newInputQueueFactory().createInputQueue(42);
    ReadableByteChannel mockChannel = Mockito.mock(ReadableByteChannel.class);
    Mockito.when(mockChannel.read(Mockito.any(ByteBuffer.class))).thenReturn(-1);

    assert (iQ.fillFrom(mockChannel) == -1);
    assert (iQ.isEmpty());
  }

  @Test
  public void fillFromDoesNotIncrementSizeIfReadThrowsException() throws IOException {
    InputQueue iQ = ProtocolFactoryProvider.newInputQueueFactory().createInputQueue(42);
    ReadableByteChannel mockChannel = Mockito.mock(ReadableByteChannel.class);
    Mockito.when(mockChannel.read(Mockito.any(ByteBuffer.class))).thenThrow(new IOException());

    assertThrows(IOException.class, () -> {
      iQ.fillFrom(mockChannel);
    });
    assert (iQ.isEmpty());
  }


  @Test
  public void fillFromShouldWorkProperly() throws IOException {
    InputQueue iQ = ProtocolFactoryProvider.newInputQueueFactory().createInputQueue(42);
    byte[] testData = "Test data".getBytes();
    ReadableByteChannel channel = Channels.newChannel(new ByteArrayInputStream(testData));

    int bytesRead = iQ.fillFrom(channel);

    assertEquals(testData.length, bytesRead);
  }



  @Test
  public void flowShouldWorkProperly() throws IOException {
    InputQueue iQ = ProtocolFactoryProvider.newInputQueueFactory().createInputQueue(42);

    byte[] thisIs =
        new byte[iQ.fillFrom(Channels.newChannel(new ByteArrayInputStream("This is ".getBytes())))];
    for (int i = 0; i < thisIs.length; i++)
      thisIs[i] = iQ.dequeue();

    byte[] a =
        new byte[iQ.fillFrom(Channels.newChannel(new ByteArrayInputStream("a ".getBytes())))];
    for (int i = 0; i < a.length; i++)
      a[i] = iQ.dequeue();

    byte[] test =
        new byte[iQ.fillFrom(Channels.newChannel(new ByteArrayInputStream("test.".getBytes())))];
    for (int i = 0; i < test.length; i++)
      test[i] = iQ.dequeue();

    assertArrayEquals("This is ".getBytes(), thisIs);
    assertArrayEquals("a ".getBytes(), a);
    assertArrayEquals("test.".getBytes(), test);
    assert (iQ.isEmpty());
  }
}
