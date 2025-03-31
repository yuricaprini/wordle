package io.github.yuricaprini.wordleclient;

/**
  * The {@code ClientConfiguration} class represents the configuration settings for the client.
  * These configuration settings are used to establish the connection with the remote server.
  * The client loads the configuration from a file and populates an instance of this class
  * accordingly.
  */
public class ClientConfiguration {

  public String registryHost;
  public Integer registryPort;
  public String serverAddress;
  public Integer serverPort;
  public String multicastGroup;
  public Integer multicastPort;
  public String multicastIF;
}
