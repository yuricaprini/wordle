package io.github.yuricaprini.wordleprotocol;

import io.github.yuricaprini.wordleprotocol.implementations.factories.InputQueueFactory;
import io.github.yuricaprini.wordleprotocol.implementations.factories.OutputQueueFactory;
import io.github.yuricaprini.wordleprotocol.implementations.factories.WordleRequestFactory;
import io.github.yuricaprini.wordleprotocol.implementations.factories.WordleResponseFactory;
import io.github.yuricaprini.wordleprotocol.ioutils.InputQueue;
import io.github.yuricaprini.wordleprotocol.ioutils.OutputQueue;
import io.github.yuricaprini.wordleprotocol.messages.WordleRequest;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse;

/**
 * The {@code ProtocolFactoryProvider} interface provides static factory methods for creating
 * instances of various protocol-related classes such as InputQueue, OutputQueue, WordleRequest,
 * and WordleResponse.
 * Implementations of these factories are provided by concrete classes.
 */
public interface ProtocolFactoryProvider {

  /**
   * Creates a new instance of InputQueue.Factory.
   *
   * @return a factory for creating InputQueue instances.
   */
  public static InputQueue.Factory newInputQueueFactory() {
    return new InputQueueFactory();
  }

  /**
   * Creates a new instance of OutputQueue.Factory.
   *
   * @return a factory for creating OutputQueue instances.
   */
  public static OutputQueue.Factory newOutputQueueFactory() {
    return new OutputQueueFactory();
  }

  /**
   * Creates a new instance of WordleRequest.Factory.
   *
   * @return a factory for creating WordleRequest instances.
   */
  public static WordleRequest.Factory newWordleRequestFactory() {
    return new WordleRequestFactory();
  }

  /**
   * Creates a new instance of WordleResponse.Factory.
   *
   * @return a factory for creating WordleResponse instances.
   */
  public static WordleResponse.Factory newWordleResponseFactory() {
    return new WordleResponseFactory();
  }
}

