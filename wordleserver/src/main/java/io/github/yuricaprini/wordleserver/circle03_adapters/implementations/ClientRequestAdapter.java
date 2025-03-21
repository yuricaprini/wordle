package io.github.yuricaprini.wordleserver.circle03_adapters.implementations;

import java.util.Objects;
import io.github.yuricaprini.wordleprotocol.messages.WordleRequest;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse;
import io.github.yuricaprini.wordleserver.circle02usecases.RegisteredUserUseCase;
import io.github.yuricaprini.wordleserver.circle03_adapters.ClientChannelFacade;
import io.github.yuricaprini.wordleserver.circle03_adapters.ClientRequestHandler;

/**
 * The {@code CommunicationAdapter} class acts as a mediator between the application layer and the 
 * business layer, translating input data coming from a {@link ClientChannelFacade} into 
 * {@link WordleRequest} and routing them to the appropriate {@link RegisteredUserUseCase}.
 */
public class ClientRequestAdapter implements ClientRequestHandler {

  private RegisteredUserUseCase.Factory useCaseFactory;
  private WordleRequest.Factory requestFactory;
  private WordleRequest currentRequest;

  /**
   * Constructs a new {@code CommunicationAdapter} with the specified {@code useCaseFactory}, and 
   * {@code requestFactory}.
   * 
   * @param useCaseFactory the factory for creating registered user use cases.
   * @param requestFactory the factory for creating wordle requests.
   * @throws NullPointerException if {@code useCaseFactory == null || requestFactory == null}.
   */
  public ClientRequestAdapter(RegisteredUserUseCase.Factory useCaseFactory,
      WordleRequest.Factory requestFactory) {

    this.useCaseFactory = Objects.requireNonNull(useCaseFactory);
    this.requestFactory = Objects.requireNonNull(requestFactory);
    this.currentRequest = requestFactory.createEmptyRequest();
  }

  @Override
  public void handleRequest(ClientChannelFacade clientChannelFacade) throws Exception {

    if (currentRequest.populateFrom(clientChannelFacade.getInputQueue())) {
      RegisteredUserUseCase usecase = useCaseFactory.createUseCase(currentRequest.getType());
      WordleResponse response = usecase.execute(currentRequest);
      response.serializeTo(clientChannelFacade.getOutputQueue());
      currentRequest = requestFactory.createEmptyRequest();
    }

  }
}
