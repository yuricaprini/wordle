package io.github.yuricaprini.wordleclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import com.google.gson.Gson;

public class Client {

  private ClientConfiguration clientConfig;
  private ResourceBundle CLIMessages;

  public Client(ResourceBundle CLIMessages) {
    this.CLIMessages = CLIMessages;
  }

  public void loadConfiguration(boolean isCustomConfig, String configName)
      throws NullPointerException, IOException {

    if (isCustomConfig)
      try (Reader reader = Files.newBufferedReader(Paths.get(configName));) {

        this.clientConfig = new Gson().fromJson(reader, ClientConfiguration.class);

      }
    else {
      try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(configName);
          Reader reader = new BufferedReader(new InputStreamReader(is));) {

        this.clientConfig = new Gson().fromJson(reader, ClientConfiguration.class);

      }
    }
  }

  public void loadRemoteServices() {//TODO fill with remoteServices
  }

  public void executeInteractiveLoop() {

    
  }

  
}
