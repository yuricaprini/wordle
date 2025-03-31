package io.github.yuricaprini.wordleserver.circle02usecases;

import io.github.yuricaprini.wordleprotocol.messages.WordleRequest;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse;

/**
 * A {@code RegisteredUserUseCase} represents a use case whose the primary actor is a registered
 * user of the Wordle application.
 * <p>
 * A use case is a task that an actor can perform with the help of the system (Wordle application).
 * <p>
 * An actor is an entity external to the system, which interacts directly with it in a certain role.
 * <p>
 * A use case is characterized by a narrative consisting of:
 * <ul>
 * <li>Description: a brief summary of the use case.
 * <li>Primary actor: actor initiating the use case (RegisteredUser in this case)
 * <li>Secondary actors: other actors who interact with the use case.
 * <li>Preconditions: conditions that must apply before the execution of the use case.
 * <li>Main sequence of events: sequence of steps of the main sequence of events (the one where
 * nothing goes wrong).
 * <li>Postconditions: conditions that must apply after the execution of the main sequence.
 * <li>Alternative sequences of events: errors, ramifications and breaks in the main sequence.
 * </ul>
 * <p>
 * 
 * @author Yuri Caprini
 */
public interface RegisteredUserUseCase {

  /**
   * Executes the main sequence of events of this use case, handling the execution of alternative 
   * sequences when necessary.
   * 
   * @param request the request containing the information necessary to execute the main sequence 
   * of events.
   * @return the response resulting from the execution of the main or alternative sequence.
   * @throws Exception
   */
  public abstract WordleResponse execute(WordleRequest request) throws Exception;

  /**
   * Abstract factory for creating instances of {@code RegisteredUserUseCase}.
   * 
   * @author Yuri Caprini
   */
  public interface Factory {

    /**
     * Creates a new instance of {@code RegisteredUserUseCase} based on the specified request type.
     * 
     * @param request the type of Wordle request for which the use case is created.
     * @return a new instance of {@code RegisteredUserUseCase}
     */
    public RegisteredUserUseCase createUseCase(WordleRequest.Type request);
  }
}
