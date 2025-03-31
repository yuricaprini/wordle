package io.github.yuricaprini.wordleprotocol.implementations;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;
import io.github.yuricaprini.wordleprotocol.exceptions.EmptyInputQueueException;
import io.github.yuricaprini.wordleprotocol.ioutils.InputQueue;

/**
 * A {@code FixedInputQueue} represents a fixed-size {@code InputQueue} for handling input bytes.
 * It maintains an internal ByteBuffer to store the input data.
 */
public class FixedInputQueue implements InputQueue {

  ByteBuffer inputBuffer;
  boolean previousCallFlag;
  int size;

  /**
   * Constructs a new instance of {@code FixedInputQueue} with the specified capacity.
   *
   * @param capacity the capacity of the input queue.
   * @throws IllegalArgumentException if {@code capacity < 0 }
   */
  public FixedInputQueue(int capacity) {
    if (capacity < 0)
      throw new IllegalArgumentException();

    this.inputBuffer = ByteBuffer.allocate(capacity);
    setPreviousCallRead();
    this.size = 0;
  }

  @Override
  public int fillFrom(ReadableByteChannel channel) throws IOException {

    Objects.requireNonNull(channel);

    if (isPreviousCallWrite())
      inputBuffer.compact();

    int readBytes = channel.read(inputBuffer);
    setPreviousCallRead();
    if (readBytes >= 0)
      size += readBytes;

    return readBytes;
  }

  @Override
  public byte dequeue() throws EmptyInputQueueException {
    if (this.isEmpty())
      throw new EmptyInputQueueException();

    if (isPreviousCallRead())
      inputBuffer.flip();

    byte result = inputBuffer.get();
    setPreviousCallWrite();
    size--;

    return result;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  private boolean isPreviousCallRead() {
    return previousCallFlag == false;
  }

  private boolean isPreviousCallWrite() {
    return previousCallFlag == true;
  }

  private void setPreviousCallRead() {
    previousCallFlag = false;
  }

  private void setPreviousCallWrite() {
    previousCallFlag = true;
  }
}
