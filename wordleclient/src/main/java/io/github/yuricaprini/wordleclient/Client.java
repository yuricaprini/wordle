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

/**
 * A {@code Client} is responsible for handling user input, and sending the corresponding requests
 * to the remote server.
 */
public class Client {

  private ClientConfiguration clientConfig;
  private ResourceBundle CLIClientMessages;
  private UserRegistrationRemoteService registrationService;

  public Client(ResourceBundle CLIClientMessages) {
    this.CLIClientMessages = CLIClientMessages;
  }

  /**
  * Loads the client configuration from a JSON file.
  * 
  * @param isCustomConfig true if the name of a custom configuration file is provided, false ow
  * @param configName the name of the configuration file
  * @throws NullPointerException if the configuration file can not be found
  * @throws IOException if an I/O error occurs while reading the configuration file
  */
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

  /**
  * Executes an interaction loop reading user input, interpreting the commands, sending the
  * corresponding requests to the remote server, and receiving its responses.
  *
  * @throws AccessException if a security issue occurs while accessing the remote service
  * @throws RemoteException if a communication-related issue occurs during the remote method call
  * @throws NotBoundException if the specified name is not currently bound
  */
  public void executeInteractionLoop() throws AccessException, RemoteException, NotBoundException {

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
  }

}
