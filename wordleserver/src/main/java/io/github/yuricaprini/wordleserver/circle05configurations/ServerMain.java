package io.github.yuricaprini.wordleserver.circle05configurations;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.rmi.Remote;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Hashtable;
import java.util.ResourceBundle;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.yuricaprini.wordleprotocol.auth.AuthToken;
import io.github.yuricaprini.wordleprotocol.remoteinterfaces.Top3NotificationRemoteService;
import io.github.yuricaprini.wordleprotocol.remoteinterfaces.UserRegistrationRemoteService;
import io.github.yuricaprini.wordleserver.circle04frameworks.DispatcherService;
import io.github.yuricaprini.wordleserver.circle04frameworks.ListenerService;
import io.github.yuricaprini.wordleserver.circle04frameworks.PersistenceService;
import io.github.yuricaprini.wordleserver.circle04frameworks.RemoteExposerService;
import io.github.yuricaprini.wordleserver.circle04frameworks.SecretWordRefresherService;

/**
 * A {@code ServerMain} represents the Wordle {@link Server} bootstrap. It is responsible for 
 * initializing all the components needed to run the server and for starting it up.
 * 
 * If there are no arguments the default server configuration 
 * name is chosen. If there is one argument, it is considered as a custom configuration name. 
 * If there are more than one, it prints an error message and exits.
 * 
 * @author Yuri Caprini
 */
public class ServerMain {

  private static final String DEFAULTBUNDLENAME = "CLIServerMessages";
  private static final String DEFAULT_CONFIG_FILENAME = "server_config.json";
  private static String customConfigName = null;
  private static ResourceBundle CLIServerMessages;

  public static void main(String[] args) {

    CLIServerMessages = ResourceBundle.getBundle(DEFAULTBUNDLENAME);

    if (args.length == 1)
      customConfigName = args[0];

    if (args.length > 1) {
      System.err.println(CLIServerMessages.getString("ERR_USAGE"));
      System.exit(1);
    }

    ServerConfiguration serverConfiguration = null;
    try {
      serverConfiguration = loadServerConfiguration();
      AuthToken.init(serverConfiguration.authTokenSecretKey, 3600000); // 1 hour
      AppConfig.init(serverConfiguration);

    } catch (FileNotFoundException e) {
      System.err.println(CLIServerMessages.getString("ERR_CONFIGNOTFOUND"));
      e.printStackTrace();
      System.exit(1);
    } catch (JsonParseException e) {
      System.err.println(CLIServerMessages.getString("ERR_CONFIGPARSINGFAIL"));
      e.printStackTrace();
      System.exit(1);
    } catch (IOException e) {
      System.err.println(CLIServerMessages.getString("ERR_CONFIGREADINGFAIL"));
      e.printStackTrace();
      System.exit(1);
    }

    try {

      Selector selector = Selector.open();
      ServerSocketChannel listeningChannel;
      listeningChannel = ServerSocketChannel.open();
      listeningChannel.bind(new InetSocketAddress(serverConfiguration.listeningPort));

      DispatcherService dispatcher = AppConfig.getNewDispatcherService(selector);
      ListenerService listener = AppConfig.getNewListenerService(listeningChannel, dispatcher);

      Hashtable<String, Remote> remoteObjs = new Hashtable<String, Remote>();
      remoteObjs.put(UserRegistrationRemoteService.class.getSimpleName(),
          AppConfig.getNewUserRegistrationRemoteService());
      remoteObjs.put(Top3NotificationRemoteService.class.getSimpleName(),
          AppConfig.getNewTop3NotificationRemoteService());
      RemoteExposerService remoteExposer =
          AppConfig.getNewRemoteExposerService(remoteObjs, serverConfiguration.registryPort);

      SecretWordRefresherService secretWordRefresherService =
          AppConfig.getNewSecretWordRefresherService(serverConfiguration.secretWordRefreshInterval);

      PersistenceService persistenceService = AppConfig.getNewPersistenceService();

      Server server = new Server.Builder(CLIServerMessages).addService(persistenceService)
          .addService(secretWordRefresherService).addService(remoteExposer).addService(dispatcher)
          .addService(listener).build();

      configureShutdownHook(server);

      server.run();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
  * Loads the server configuration from a JSON file. Uses the file name provided as a command line
  * argument if present, otherwise uses the default configuration name.
  *
  * @return ServerConfiguration object containing the server configuration.
  * @throws FileNotFoundException if the configuration file is not found.
  * @throws JsonParseException    if an error occurs during JSON file parsing.
  * @throws IOException           if an I/O error occurs while reading the file.
  */
  private static ServerConfiguration loadServerConfiguration()
      throws FileNotFoundException, JsonParseException, IOException {

    try (Reader reader = customConfigName != null ? new FileReader(customConfigName)
        : new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(DEFAULT_CONFIG_FILENAME)));) {

      return new GsonBuilder().registerTypeAdapter(LocalTime.class, new LocalTimeSerializer())
          .registerTypeAdapter(LocalTime.class, new LocalTimeDeserializer())
          .setDateFormat("HH:mm:ss").create().fromJson(reader, ServerConfiguration.class);
    }
  }

  /**
  * Configures a shutdown hook to handle the server shutdown gracefully when Ctrl+C is pressed.
  *
  * @param server the server to be shut down.
  */
  private static void configureShutdownHook(Server server) {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        server.shutdown();
      } catch (InterruptedException e) {
        //never happens, since no one interrupts the shutdown thread
      }
    }));
  }

  private static class LocalTimeSerializer implements JsonSerializer<LocalTime> {
    @Override
    public JsonPrimitive serialize(LocalTime localTime, Type type,
        JsonSerializationContext jsonSerializationContext) {
      return new JsonPrimitive(localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
  }

  private static class LocalTimeDeserializer implements JsonDeserializer<LocalTime> {
    @Override
    public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      return LocalTime.parse(json.getAsString(), DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
  }
}
