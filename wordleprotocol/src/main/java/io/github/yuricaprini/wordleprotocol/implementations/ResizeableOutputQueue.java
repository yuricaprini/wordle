package io.github.yuricaprini.wordleprotocol.implementations;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.LinkedList;
import java.util.Objects;
import io.github.yuricaprini.wordleprotocol.ioutils.OutputQueue;

/**
 * A {@code ResizeableOutputQueue} implements the {@link OutputQueue} interface for buffering
 * data destined for a {@link WritableByteChannel}. It dynamically resizes to accommodate varying 
 * data sizes.
 */

public class ResizeableOutputQueue implements OutputQueue {

  LinkedList<ByteBuffer> queue;

  /**
   * Constructs a new instance of {@code ResizeableOutputQueue}.
   * The queue is initially empty and can dynamically resize to accommodate data.
   */
  public ResizeableOutputQueue() {
    queue = new LinkedList<ByteBuffer>();
  }

  @Override
  public int drainTo(WritableByteChannel channel) throws IOException {

    Objects.requireNonNull(channel);

    int totalWrittenBytes = 0;

    while (!queue.isEmpty()) {

      ByteBuffer current = queue.peekFirst();
      int currentWrittenBytes = channel.write(current);
      totalWrittenBytes += currentWrittenBytes;

      if (!current.hasRemaining())
        queue.removeFirst();
      else
        break;
    }

    return totalWrittenBytes;
  }

  @Override
  public boolean isEmpty() {
    return queue.isEmpty();
  }

  @Override
  public boolean enqueue(byte[] outData) {

    if (Objects.requireNonNull(outData).length == 0)
      return false;

    queue.addLast(ByteBuffer.wrap(outData));

    return true;
  }
}
