package io.github.yuricaprini.wordleserver.circle04frameworks;

/**
 * A {@code RegistrationFacade} serves as the interface enabling other services within the Wordle 
 * server to register channels along with their coupled channel handler to a 
 * {@link DispatcherService}.
 * <p>
 * Methods provided by this interface are thread-safe.
 * 
 * @author Yuri Caprini
 */
public interface RegistrationFacade {

  /**
   * Registers the channel encapsulated by its {@code handler} to the inner {@code Selector} of the 
   * {@code DispatcherService} implementing this interface. 
   * <p>
   * This method is thread safe and blocks until the registration is successful.
   * 
   * @param channelHandler the channel handler encapsulating the channel that has to be registered.
   * @throws NullPointerException if {@code handler==null}.
   * @throws IllegalArgumentException if the channel encapsulated by this channel handler is in 
   *         blocking mode.
   * @throws InterruptedException if the calling thread is interrupted during registration. 
   * @throws Exception if a generic error occurs during registration.
   */
  public void register(ChannelHandler channelHandler)
      throws IllegalArgumentException, InterruptedException, Exception;
}
