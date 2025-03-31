package io.github.yuricaprini.wordleserver.circle04frameworks;

import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.util.Objects;
import io.github.yuricaprini.wordleserver.circle05configurations.Service;

/**
 * A {@code ListenerService} is a {@link Service} within Wordle server that continuously listens
 * for incoming connection requests from Wordle clients.
 * <p>
 * When a connection request arrives, it's accepted, and the channel established with the client is
 * registered to a {@link DispatcherService} using its {@link RegistrationFacade}.
 * 
 * @author Yuri Caprini
 */
public abstract class ListenerService implements Service {

  protected ServerSocketChannel listeningChannel;
  protected RegistrationFacade registrationFacade;
  protected ChannelHandler.Factory handlerFactory;

  private Thread currentThread;
  private boolean isUpAndRunning;

  /**
   * Abstract {@code ListenerService} constructor. Its role is to enforce a minimum setup for
   * listeners constructed by subclasses. Each newly constructed listener must have at least 
   * a {@code listeningChannel} to listen for incoming client connection requests, a 
   * {@code registrationFacade} for registering established client channels to a dispatcher,
   * a {@code handlerFactory} to create handlers to pair with channels at registration time.
   * 
   * @param listeningChannel the listening channel used by the newly created listener service.
   * @param registrationFacade the registration facade used by the newly created listener service.
   * @param handlerFactory the channel handler factory used by the newly created listener service.
   * 
   * @throws NullPointerException if {@code listeningChannel==null || registrationFacade == null || *         handlerFactory == null }  
   * @throws IllegalArgumentException if {@code listeningChannel} is not open or in non-blocking
   *         mode.
   */
  protected ListenerService(ServerSocketChannel listeningChannel,
      RegistrationFacade registrationFacade, ChannelHandler.Factory handlerFactory) {

    if (!Objects.requireNonNull(listeningChannel).isOpen() || !listeningChannel.isBlocking())
      throw new IllegalArgumentException();

    this.listeningChannel = listeningChannel;
    this.registrationFacade = Objects.requireNonNull(registrationFacade);
    this.handlerFactory = Objects.requireNonNull(handlerFactory);
  }

  /**
   * Performs an infinite loop, continuosly listening for new connection requests from clients.
   * <p>
   * When a connection request arrives, it's accepted and the established client channel is 
   * registered to a {@code DispatcherService} using its {@code RegistrationFacade}.
   * <p>
   * When the loop ends due to an interruption or an exception all the resources used by this 
   * service are released.
   * 
   * @throws Exception if a generic exception occurs during execution.
   */
  @Override
  public ListenerService call() throws Exception {

    try {
      initState();
      notifyAllIsUpAndRunning();

      while (!Thread.interrupted()) {
        try {
          this.listen();
        } catch (InterruptedException | ClosedByInterruptException e) {
          break;
        }
      }

    } finally {

      try {
        listeningChannel.close();
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }

    return this;
  }

  @Override
  public String getName() {
    return "Listener";
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
   * Blocks until a connection request arrives, then accepts it, and register the established 
   * channel with the client, along with the corrisponding handler, to a {@code DispatcherService} 
   * using its {@code RegistrationFacade}.
   * 
   * @throws InterruptedException if the calling thread is interrupted.
   * @throws ClosedByInterruptException if the channel is closed due to an interrupt.
   * @throws Exception if a generic error occurs while listening.
   */
  protected abstract void listen()
      throws InterruptedException, ClosedByInterruptException, Exception;

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
