package io.github.yuricaprini.wordleprotocol.ioutils;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import io.github.yuricaprini.wordleprotocol.exceptions.EmptyInputQueueException;

/**
 * An {@code InputQueue} represents a queue for handling input bytes coming from a specified 
 * {@link ReadableByteChannel}.
 */
public interface InputQueue {

  /**
   * Fills the queue with data read from the specified {@code channel} as much as possible.
   *
   * @param channel the channel to read data from.
   * @return the number of bytes read and added to the queue, {@code -1} if channel has reached EOS.
   * @throws NullPointerException if {@code channel == null}
   * @throws IOException if an I/O error occurs while reading from the channel.
   */
  public int fillFrom(ReadableByteChannel channel) throws IOException;

  /**
   * Dequeues a byte from the input queue.
   *
   * @return the dequeued byte.
   * @throws EmptyInputQueueException if the queue is empty.
   */
  public byte dequeue();

  /**
    * Checks whether the input queue is empty.
    *
    * @return {@code true} if the queue is empty, {@code false} otherwise.
  */
  public boolean isEmpty();

  /**
   * Abstract Factory for creating instances of {@code InputQueue}
   */
  public interface Factory {

    /**
     * Creates a new instance of {@code InputQueue} with the specified capacity.
     *
     * @param capacity the capacity of the newly created queue.
     * @throws IllegalArgumentException if {@code capacity < 0 }
     * @return a new instance of {@code InputQueue}.
     */
    public InputQueue createInputQueue(int capacity);
  }
}
