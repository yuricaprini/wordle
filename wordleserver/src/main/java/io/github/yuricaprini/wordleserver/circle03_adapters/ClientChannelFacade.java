package io.github.yuricaprini.wordleserver.circle03_adapters;

import io.github.yuricaprini.wordleprotocol.ioutils.InputQueue;
import io.github.yuricaprini.wordleprotocol.ioutils.OutputQueue;

/**
 * The {@code ClientChannelFacade} interface offers a streamlined interface for accessing input and * output data on the client channel.
 */
public interface ClientChannelFacade {

  /**
   * Returns the {@link InputQueue} containing the client channel input data.
   * 
   * @return the {@code InputQueue} containing the client channel input data.
   */
  public InputQueue getInputQueue();

  /**
   * Returns the {@link OutputQueue} containing the client channel output data.
   * 
   * @return the {@code OutputQueue} containing the client channel output data.
   */
  public OutputQueue getOutputQueue();
}
