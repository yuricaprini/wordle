package io.github.yuricaprini.wordleclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Scanner;
import com.google.gson.Gson;
import io.github.yuricaprini.winsomeprotocol.remoteinterfaces.UserRegistrationRemoteService;

public class Client {

  private ClientConfiguration clientConfig;
  private ResourceBundle CLIClientMessages;
  private UserRegistrationRemoteService registrationService;

  public Client(ResourceBundle CLIClientMessages) {
    this.CLIClientMessages = CLIClientMessages;
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

  public void executeInteractiveLoop() throws AccessException, RemoteException, NotBoundException {

    try (Scanner scanner = new Scanner(System.in)) {

      boolean quit = false;
      while (!quit) {

        String input = scanner.nextLine();
        String[] tokens = input.split(" ");
        String command = tokens[0];
        String[] commandArgs = Arrays.copyOfRange(tokens, 1, tokens.length);

        switch (command) {

          case "register":

            if (commandArgs.length != 2)
              System.err.println(CLIClientMessages.getString("ERR_REGISTER_N_ARGS"));
            else {

              if (this.registrationService == null) {
                this.registrationService = (UserRegistrationRemoteService) LocateRegistry
                    .getRegistry(clientConfig.registryHost, clientConfig.registryPort)
                    .lookup(UserRegistrationRemoteService.class.getName());
              }

              switch (this.registrationService.registerUser(commandArgs[0], commandArgs[1])) {
                case OK:
                  System.out.println(CLIClientMessages.getString("OUT_REGISTER_OK"));
                  break;
                case USERNAME_SHORT:
                  System.err.println(CLIClientMessages.getString("ERR_REGISTER_USERNAME_SHORT"));
                  break;
                case USERNAME_LONG:
                  System.err.println(CLIClientMessages.getString("ERR_REGISTER_USERNAME_LONG"));
                  break;
                case USERNAME_SPACE:
                  System.err.println(CLIClientMessages.getString("ERR_REGISTER_USERNAME_SPACE"));
                  break;
                case PASSWORD_SHORT:
                  System.err.println(CLIClientMessages.getString("ERR_REGISTER_PASSWORD_SHORT"));
                  break;
                case PASSWORD_LONG:
                  System.err.println(CLIClientMessages.getString("ERR_REGISTER_PASSWORD_LONG"));
                  break;
                case PASSWORD_SPACE:
                  System.err.println(CLIClientMessages.getString("ERR_REGISTER_PASSWORD_SPACE"));
                  break;
                case PASSWORD_NO_DIGIT:
                  System.err.println(CLIClientMessages.getString("ERR_REGISTER_PASSWORD_NO_DIGIT"));
                  break;
                case PASSWORD_NO_UC:
                  System.err.println(CLIClientMessages.getString("ERR_REGISTER_PASSWORD_NO_UC"));
                  break;
                case ALREADY_REGISTERED:
                  System.err
                      .println(CLIClientMessages.getString("ERR_REGISTER_ALREADY_REGISTERED"));
                  break;
                default: //handled, but never occurs
                  System.err.println(CLIClientMessages.getString("ERR_REGISTER_UNKNOWN_OUTCOME"));
                  break;
              }
            }
            break;

          case "quit":
            quit = true;
            break;

          default:
            break;
        }
      }
    }
  }

  public class ClientConfiguration {

    public String registryHost;
    public Integer registryPort;
    public String serverAddress;
    public Integer serverPort;
  }

}
