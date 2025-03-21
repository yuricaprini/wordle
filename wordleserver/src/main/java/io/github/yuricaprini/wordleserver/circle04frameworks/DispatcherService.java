package io.github.yuricaprini.wordleserver.circle04frameworks;

import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;
import java.util.Objects;
import io.github.yuricaprini.wordleserver.circle05configurations.Server;
import io.github.yuricaprini.wordleserver.circle05configurations.Service;

/**
 * A {@code DispatcherService} is a {@link Service} within Wordle {@link Server} which is
 * responsible for monitoring the readiness of each {@link SelectableChannel}, registered via its 
 * {@link RegistrationFacade} to its inner {@link Selector}, to perform low-level I/O operations 
 * that it is interested in. 
 * <p>
 * At registration, each channel is paired with a {@link ChannelHandler} which remains the
 * same for its entire life-cycle, until the channel is closed.
 * <p>
 * At every moment, the dispatcher knows what are the next low-level I/O operations that a channel 
 * is interested to perform from its paired channel handler.
 * <p>
 * The dispatcher monitors the state of the channel, and when the latter is ready to perform an I/O 
 * operation of interest, it delegates its management to its associated channel handler who is
 * responsible for performing that operation according to the application business rules.
 * 
 * @author Yuri Caprini
 */
public abstract class DispatcherService implements Service, RegistrationFacade {

  private Thread currentThread;
  private boolean isUpAndRunning;
  protected Selector selector;

  /**
   * Abstract {@code DispatcherService} constructor. Its role is to enforce a minimum setup for
   * dispatchers constructed by subclasses. Each newly constructed dispatcher must have at least 
   * a {@code Selector} to multiplex the channels registered to it.
   * 
   * @param selector the selector used by the newly created dispatcher.
   * 
   * @throws NullPointerException if {@code selector == null}
   * @throws IllegalArgumentException if {@code selector} is not open.
   * 
   */
  protected DispatcherService(Selector selector) {

    if (!Objects.requireNonNull(selector).isOpen())
      throw new IllegalArgumentException();

    this.selector = selector;
  }

  /**
   * Performs an infinite loop, monitoring the readiness to perform I/O operations of the registered
   * channels and dispatching them, when ready, to their corresponding channel handlers in order to
   * execute them according to the application business rules.
   * <p>
   * When the loop ends due to an interruption or to an exception, initiates a cleanup procedure 
   * for the graceful closure of the service which has the possibility of finishing the last 
   * activities and freeing up the resources.
   * 
   * @return this instance of {@code DispatcherService}.
   * @throws Exception if an error occurs during service lifecycle.
   */
  @Override
  public DispatcherService call() throws Exception {

    initState();
    notifyAllIsUpAndRunning();

    try {

      while (!Thread.interrupted())
        this.dispatch();

    } finally {
      try {
        this.cleanup();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return this;
  }

  @Override
  public String getName() {
    return "Dispatcher";
  }

  @Override
  public void shutdown() {
    currentThread.interrupt();
  }

  @Override
  public void awaitIsUpAndRunning() throws InterruptedException {
    waitIsUpAndRunning();
  }

  @Override
  public void awaitTermination() throws InterruptedException {
    currentThread.join();
  }

  /**
   * Performs a round of selection using the dispatcher inner {@code Selector}:
   * 
   * <ul>
   * <li>Selects the channels that are ready to perform some low level I/O operation they have 
   * previously expressed interest in.
   * <li>For each selected channel, the corresponding channel handler is executed. This one takes 
   * care of executing those operations the channel is ready to perform, according to the
   * application business rules.
   * <li>When a channel handler has terminated its execution, it tells the dispatcher what are the 
   * next I/O operation the channel will be interested to perform.
   * </ul>
   * 
   * @throws Exception if a generic exception occurs during dispatching.
   */
  protected abstract void dispatch() throws Exception;

  /**
  * Completes ongoing activities and releases the resources.
  * 
  * @throws Exception if a generic exception occurs during cleaning.
  */
  protected abstract void cleanup() throws Exception;

  private void initState() {
    this.currentThread = Thread.currentThread();
    this.isUpAndRunning = false;
  }

  private void waitIsUpAndRunning() throws InterruptedException {
    synchronized (this) {
      while (!isUpAndRunning) {
        this.wait();
      }
    }
  }

  private void notifyAllIsUpAndRunning() {
    synchronized (this) {
      isUpAndRunning = true;
      this.notifyAll();
    }
  }
}
