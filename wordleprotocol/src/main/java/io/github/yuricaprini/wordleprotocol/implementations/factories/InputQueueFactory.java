package io.github.yuricaprini.wordleprotocol.implementations.factories;

import io.github.yuricaprini.wordleprotocol.implementations.FixedInputQueue;
import io.github.yuricaprini.wordleprotocol.ioutils.InputQueue;

/**
 * An implementation of {@link InputQueue.Factory} using {@link FixedInputQueue} with specified 
 * capacity.
 */
public class InputQueueFactory implements InputQueue.Factory {

  @Override
  public InputQueue createInputQueue(int capacity) {
    return new FixedInputQueue(capacity);
  }
}
