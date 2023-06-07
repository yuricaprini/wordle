// package io.github.yuricaprini.wordleserver.circle05configurations;

// import java.io.IOException;
// import java.io.Reader;
// import java.net.InetSocketAddress;
// import java.nio.channels.Selector;
// import java.nio.channels.ServerSocketChannel;
// import java.nio.file.Files;
// import java.nio.file.InvalidPathException;
// import java.nio.file.Paths;
// import com.google.gson.Gson;
// import com.google.gson.JsonIOException;
// import com.google.gson.JsonSyntaxException;

// /**
//  * This is the Winsome server bootstrap class. It creates, initializes and starts the execution of
//  * all the components needed to run the application.
//  */
// public class ServerMain {

//   public static final String OUT_SRV_HELLO = "*** Server started ***";
//   public static final String OUT_STARTSHUTDOWN = "\n*** Starting shutdown procedure ***";
//   public static final String OUT_SRV_GOODBYE = "*** Server terminated gracefully ***";
//   public static final String ERR_SRV_INVALIDARGSNUMBER = "Err: invalid arguments number";
//   public static final String ERR_SRV_OPENINGRESOURCES = "Err: opening resources";
//   public static final String ERR_CONFIG_INVALIDPATH = "Err: invalid configuration file path";
//   public static final String ERR_CONFIG_OPENINGJSONFILE = "Err: opening the configuration file";
//   public static final String ERR_CONFIG_READINGJSONFILE = "Err: reading the configuration file";
//   public static final String ERR_CONFIG_MALFORMEDJSONFILE = "Err: malformed configuration file";
//   public static final String ERR_CONFIG_EMPTYFILE = "Err: empty configuration file";
//   public static final String ERR_CONFIG_CORRECT = "Err: incorrect fields in configuration file";
//   public static final String ERR_CONFIG_SECURITY = "Error: no access rights for configuration file";
//   public static final String USAGE = "Usage: java -jar [WinsomeServer.jar] " + "[config.json] ";
//   public static final int MIN_SNAPSHOTINTERVAL = 1;

//   public static void main(String[] args) {

//     ServerServicesOrchestrator serverExecutor = null;
//     ServerConfiguration serverConfiguration = null;

//     if (args.length != 1) {
//       System.err.println(ERR_SRV_INVALIDARGSNUMBER);
//       System.err.println(USAGE);
//       return;
//     }

//     try {
//       serverConfiguration = loadConfiguration(args[0]);

//     } catch (InvalidPathException ipe) {
//       System.err.println(ERR_CONFIG_INVALIDPATH);
//       System.err.println(ipe.getMessage());
//       System.err.println(USAGE);
//       return;

//     } catch (IOException ioe) {
//       System.err.println(ERR_CONFIG_OPENINGJSONFILE);
//       System.err.println(ioe.getMessage());
//       System.err.println(USAGE);
//       return;

//     } catch (SecurityException se) {
//       System.err.println(ERR_CONFIG_SECURITY);
//       System.err.println(se.getMessage());
//       System.err.println(USAGE);
//       return;

//     } catch (JsonIOException jIOe) {
//       System.err.println(ERR_CONFIG_READINGJSONFILE);
//       System.err.println(jIOe.getMessage());
//       System.err.println(USAGE);
//       return;
//     } catch (JsonSyntaxException jse) {
//       System.err.println(ERR_CONFIG_MALFORMEDJSONFILE);
//       System.err.println(jse.getMessage());
//       System.err.println(USAGE);
//       return;
//     }

//     if (serverConfiguration == null) {
//       System.err.println(ERR_CONFIG_EMPTYFILE);
//       System.err.println(USAGE);
//       return;
//     }

//     if (!checkConfiguration(serverConfiguration)) {
//       System.err.println(ERR_CONFIG_CORRECT);
//       System.err.println(USAGE);
//       return;
//     }

//     try {
//       serverExecutor = buildServerExecutor(serverConfiguration);
//     } catch (IOException e) {
//       System.err.println(ERR_SRV_OPENINGRESOURCES);
//       System.err.println(e.getMessage());
//       System.err.println(USAGE);
//       return;
//     }
//     configureShutDown(serverExecutor);

//     System.out.println(OUT_SRV_HELLO);
//     serverExecutor.runServices();
//   }

//   // ------------------------------------------------------------------------------------------------
//   // Private convenience methods
//   // ------------------------------------------------------------------------------------------------

//   /**
//    * Loads the server configuration file from {@code configFilePath}
//    * 
//    * @param configFilePath the file path string
//    * @return the object representing the server configuration
//    * @throws InvalidPathException if the path string cannot be converted to a {@code Path}
//    * @throws IOException if an I/O error occurs opening the file
//    * @throws SecurityException if there are no rights to read the file
//    * @throws JsonIoException if error occurs reading the file
//    * @throws JsonSyntaxException if the json representation of server configuration is malformed
//    */
//   private static ServerConfiguration loadConfiguration(String configFilePath)
//       throws InvalidPathException, IOException, SecurityException, JsonIOException,
//       JsonSyntaxException {

//     Gson gson = new Gson();
//     try (Reader reader = Files.newBufferedReader(Paths.get(configFilePath));) {
//       return gson.fromJson(reader, ServerConfiguration.class);
//     }
//   }

//   /**
//    * Checks if {@code serverConfiguration} fields are been properly filled: no fields should be null
//    * or have a invalid value for business logic.
//    * 
//    * @param serverConfiguration the object representing the server configuration
//    * @return {@code true} if all {@code serverConfiguration} fields are !=null and
//    *         {@code serverConfiguration.snapshotInterval >= } {@value #MIN_SNAPSHOTINTERVAL}
//    */
//   private static boolean checkConfiguration(ServerConfiguration serverConfiguration) {
//     return (serverConfiguration.targetDirectory != null
//         && serverConfiguration.snapshotInterval != null
//         && serverConfiguration.snapshotInterval >= MIN_SNAPSHOTINTERVAL);
//   }

//   /**
//    * Creates and initializes all the components needed to create a server executor and then creates
//    * it.
//    * 
//    * @return the newly created server executor
//    * @throws IOException
//    */
//   private static ServerServicesOrchestrator buildServerExecutor(ServerConfiguration serverConfiguration)
//       throws IOException {

//     String targetDirectory = serverConfiguration.targetDirectory;
//     int snapshotInterval = serverConfiguration.snapshotInterval;

//     PersistenceService persistenceService = FactoryProvider.getPersistenceServiceFactory()
//         .createPersistenceService(FactoryProvider.getServerStatusFactory().createServerStatus(),
//             snapshotInterval, targetDirectory);

//     RemoteServicesExposer remoteServicesExposer = FactoryProvider.getRemoteServicesExposerFactory()
//         .createRemoteServicesExposer(FactoryProvider.getRemoteUserRegistrationFactory()
//             .createUserRegistrationRemoteService(), serverConfiguration.registryPort);

//     NIODispatcher nioDispatcher = FactoryProvider.getNIODispatcherFactory()
//         .createNIODispatcher(Selector.open(), FactoryProvider.getChannelHandlerFactory());

//     ServerSocketChannel listeningChannel = ServerSocketChannel.open();
//     listeningChannel.bind(new InetSocketAddress(serverConfiguration.serverTCPAddress,
//         serverConfiguration.serverTCPPort));
//     NIOListener nioListener =
//         FactoryProvider.getNIOListenerFactory().createNIOListener(listeningChannel, nioDispatcher);

//     return new ServerServicesOrchestrator(persistenceService, remoteServicesExposer, nioListener,
//         nioDispatcher);
//   }

//   /**
//    * Configures the sequence of actions to be performed when server termination is invoked.
//    * 
//    * @param serverExecutor the {@code ServerExecutor} called to shutdown services when server
//    *        termination is invoked
//    */
//   private static void configureShutDown(ServerServicesOrchestrator serverExecutor) {

//     final ServerServicesOrchestrator finalServerExecutor = serverExecutor;
//     Thread gracefulShutDown = new Thread(new Runnable() {
//       @Override
//       public void run() {
//         System.out.println(OUT_STARTSHUTDOWN);
//         try {
//           finalServerExecutor.shutdownServices();
//         } catch (InterruptedException e) {
//           // this branch is never reached since after shutdown hook is called
//           // no other interrupts are taken by JVM
//         }
//         System.out.println(OUT_SRV_GOODBYE);
//       }
//     });
//     Runtime.getRuntime().addShutdownHook(gracefulShutDown);
//   }
// }
