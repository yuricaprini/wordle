package io.github.yuricaprini.wordleprotocol.ioutils;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

/**
 * A {@code OutputQueue} represents a queue for buffering output data destined for a designated
 * {@link WritableByteChannel}.
 */
public interface OutputQueue {

  /**
     * Writes queued data to the specified channel. This method continues writing from the queue to 
     * the channel until either the queue becomes empty or an attempt to write {@code n} bytes
     * results in writing {@code h < n} bytes.
     *
     * @param channel the channel to which data will be written.
     * @return the total number of bytes written to the channel.
     * @throws NullPointerException if {@code channel == null}
     * @throws IOException if an I/O error occurs while writing to the channel.
     */
  public int drainTo(WritableByteChannel channel) throws IOException;

  /**
    * Enqueues the bytes contained in the specified byte array.
    *
    * @param outData the buffer containing the bytes to enqueue.
    * @return {@code true} if the bytes in {@code outData} are successfully enqueued, 
    * {@code false} if {@code outData.length == 0}
    * @throws NullPointerException if {@code outData == null}
    */
  public boolean enqueue(byte[] outData);

  /**
    * Checks whether the output queue is empty.
    *
    * @return {@code true} if the queue is empty, {@code false} otherwise.
    */
  public boolean isEmpty();

  /**
   * Abstract Factory for creating instances of {@code OutputQueue}
   */
  public interface Factory {

    /**
     * Creates a new instance of {@code OutputQueue}.
     *
     * @return a new instance of {@code OutputQueue}.
     */
    public OutputQueue createOutputQueue();
  }
}
