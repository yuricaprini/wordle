package io.github.yuricaprini.wordleserver.circle05configurations;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ResourceBundle;
import com.google.gson.Gson;

public class Server {

  private ServerConfiguration serverConfig;

  private ResourceBundle CLIServerMessages;

  public void Server(ResourceBundle CLIServerMessages) {

  }

  public void loadConfiguration(boolean isCustomConfig, String configName) throws IOException {
    try (Reader reader = isCustomConfig ? new FileReader(configName)
        : new BufferedReader(new InputStreamReader(
            this.getClass().getClassLoader().getResourceAsStream(configName)));) {

      this.serverConfig = new Gson().fromJson(reader, ServerConfiguration.class);
    }
  }

}


class ServerConfiguration {

  //Persistence service
  public String targetDirectory;
  public Integer snapshotInterval;

  //RMI services
  public Integer registryPort;

  //TCP connection for request-response
  public String serverTCPAddress;
  public Integer serverTCPPort;
}
