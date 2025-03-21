package io.github.yuricaprini.wordleserver.circle05configurations;

/**
  * A {@code ServerConfiguration} contains the parameters needed to configure the server, which
  * will be populated with values ​​loaded from a configuration file.
*/
public class ServerConfiguration {

  public Integer registryPort;
  public Integer listeningPort;
  public Long secretWordRefreshInterval;
  public String authTokenSecretKey;
  public String multicastGroup;
  public Integer multicastPort;
  public String usersFileName;

}
