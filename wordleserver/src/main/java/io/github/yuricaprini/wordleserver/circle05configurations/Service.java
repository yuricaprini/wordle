package io.github.yuricaprini.wordleserver.circle05configurations;

import java.util.concurrent.Callable;

/**
 * A {@code Service} represents a service managed by Wordle {@link Server}.
 */
public interface Service extends Callable<Service> {

  /**
  * Retrieves the name of this service.
  *
  * @return the name of this service as a string.
  */
  public String getName();

  /**
   * Initiates an orderly shutdown in which this service finishes executing started tasks, but 
   * refuses to start new ones.
   */
  public void shutdown();

  /**
   * Blocks until this service is terminated after a shutdown request.
   * 
   * @throws InterruptedException if interrupted while waiting.
   */
  public void awaitTermination() throws InterruptedException;

  /**
   * Blocks until this service has completed its initialization routine and is up and running. 
   * 
   * @throws InterruptedException if interrupted while waiting. 
   */
  public void awaitIsUpAndRunning() throws InterruptedException;

}
