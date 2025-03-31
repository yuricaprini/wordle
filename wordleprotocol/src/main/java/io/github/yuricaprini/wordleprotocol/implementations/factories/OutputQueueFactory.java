package io.github.yuricaprini.wordleprotocol.implementations.factories;

import io.github.yuricaprini.wordleprotocol.implementations.ResizeableOutputQueue;
import io.github.yuricaprini.wordleprotocol.ioutils.OutputQueue;

/**
 * An implementation of {@link OutputQueue.Factory} using {@code ResizableInputQueue}.
 */
public class OutputQueueFactory implements OutputQueue.Factory {

  @Override
  public OutputQueue createOutputQueue() {
    return new ResizeableOutputQueue();
  }
}
