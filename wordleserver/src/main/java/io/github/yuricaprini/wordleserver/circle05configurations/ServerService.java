package io.github.yuricaprini.wordleserver.circle05configurations;

import java.util.concurrent.Callable;

/**
 * The {@code ServerService} interface represents a service in the WordleServer application.
 * 
 * @author Yuri Caprini
 */
public interface ServerService extends Callable<ServerService> {

  /**
  * Retrieves the name of this service.
  * @return The name of this service as a String.
  */
  public String getServiceName();

  /**
   * Cleans up or releases any resources associated with this service.
   * Implementations of this method should handle the appropriate cleanup operations specific to   * thisservice.
   */
  public void cleanupServiceResources();

}
