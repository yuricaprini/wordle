package io.github.yuricaprini.wordleclient;

import java.io.IOException;
import java.util.ResourceBundle;

public class ClientMain {

  private static final String DEFAULTBUNDLENAME = "CLIMessages";
  private static final String DEFAULTCONFIGNAME = "client_config.json";

  public static void main(String[] args) {

    ResourceBundle CLIMessages = ResourceBundle.getBundle(DEFAULTBUNDLENAME);
    boolean isCustomConfig = false;
    String configName = DEFAULTCONFIGNAME;

    if (args.length == 1) {
      isCustomConfig = true;
      configName = args[0];
    }

    if (args.length > 1){
      System.err.println(CLIMessages.getString("USAGE"));
      System.exit(1);
    }

    Client client = new Client(CLIMessages);
    
    try {
      client.loadConfiguration(isCustomConfig, configName);
    } catch (NullPointerException | IOException e) {
      System.err.println(CLIMessages.getString("CONFIG_LOAD_FAIL"));
      e.printStackTrace();
      System.exit(1);
    }

    client.loadRemoteServices();

    client.executeInteractiveLoop();
  }
}
