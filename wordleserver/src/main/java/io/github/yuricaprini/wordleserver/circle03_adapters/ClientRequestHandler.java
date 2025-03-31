package io.github.yuricaprini.wordleserver.circle03_adapters;

/**
 * A {@code ClientRequestHandler} is responsible for parsing input data from the specified 
 * {@code clientChannelFacade}, routing the incoming client requests accordingly.
 */
public interface ClientRequestHandler {

  /**
   * Parses the input data from the specified {@code clientChannelFacade}, routing the incoming 
   * client requests.
   *
   * @param clientChannelFacade the client channel facade providing access to the communication 
   * channel with the client.
   * @throws Exception if an error occurs while handling requests.
   */
  public void handleRequest(ClientChannelFacade clientChannelFacade) throws Exception;

}
