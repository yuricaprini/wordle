// package io.github.yuricaprini.winsomeserver.circle05configurations;

// import java.util.concurrent.ExecutionException;
// import java.util.concurrent.FutureTask;
// import io.github.yuricaprini.winsomeserver.circle04frameworks.NIODispatcher;
// import io.github.yuricaprini.winsomeserver.circle04frameworks.NIOListener;
// import io.github.yuricaprini.winsomeserver.circle04frameworks.PersistenceService;
// import io.github.yuricaprini.winsomeserver.circle04frameworks.RemoteServicesExposer;
// import io.github.yuricaprini.winsomeserver.circle04frameworks.exceptions.CannotLoadServerStatusException;
// import io.github.yuricaprini.winsomeserver.circle04frameworks.exceptions.CleanUpResourcesException;
// import io.github.yuricaprini.winsomeserver.circle04frameworks.exceptions.RemoteServicesExposerException;

// /**
//  * A {@code ServerExecutor} is the orchestrator of the main services that make up the Winsome
//  * server. It manages the start-up and shutdown of these services, handling any exceptions that
//  * occur during their execution.
//  * 
//  * @author Yuri Caprini
//  *
//  */
// public class ServerServicesOrchestrator {

//   public static final String OUT_PSTSERV_GOODBYE = "--- Persistence service terminated ---";
//   public static final String OUT_PSTSERV_STATUSLOADED = "--- Server status loaded ---";
//   public static final String ERR_PSTSERV_INTERRUPTED = "Err: persistence service interrupted";
//   public static final String ERR_PSTSERV_ABORTED = "Err: persistence service aborted by exception";
//   public static final String ERR_PSTSERV_LOADING = "Err: persistence service cannot load";

//   private final PersistenceService persistenceService;
//   private final RemoteServicesExposer remoteServiceExposer;
//   private final NIOListener nioListener;
//   private final NIODispatcher nioDispatcher;

//   private final Thread persistenceServiceThread;
//   private final Thread nioListenerThread;
//   private final Thread nioDispatcherThread;

//   /**
//    * Constructs a new {@code ServerExecutor} with references to services it has to orchestrate.
//    * 
//    * @param persistenceService service that guarantees the persistence of the winsome server status
//    * @param remoteServicesExposer component that exposes winsome server remote services
//    * @param periodicService service that periodically runs a set of predifined operations
//    * @param nioListener service listening for connection requests from clients
//    * @param nioDispatcher service that monitors I/O client channels and dispatches them to their
//    *        relatives handlers, when they are ready to perform some I/O operation
//    * @throws NullPointerException if
//    *         {@code persistenceService == null || remoteServicesExposer == null || 
//    *          || nioListener == null || nioDispatcher == null}
//    */
//   public ServerServicesOrchestrator(PersistenceService persistenceService,
//       RemoteServicesExposer remoteServicesExposer, NIOListener nioListener,
//       NIODispatcher nioDispatcher) {

//     if (persistenceService == null || remoteServicesExposer == null || nioListener == null
//         || nioDispatcher == null)
//       throw new NullPointerException();

//     this.persistenceService = persistenceService;
//     this.remoteServiceExposer = remoteServicesExposer;
//     this.nioListener = nioListener;
//     this.nioDispatcher = nioDispatcher;

//     this.persistenceServiceThread = new Thread(new PersistenceServiceTask(this.persistenceService));
//     this.nioListenerThread = new Thread(new NIOListenerTask(this.nioListener));
//     this.nioDispatcherThread = new Thread(new NIODispatcherTask(this.nioDispatcher));
//   }

//   /**
//    * Starts the services in the correct order.
//    */
//   public void runServices() {

//     try {
//       persistenceService.loadBeforeExecution();

//     } catch (CannotLoadServerStatusException e) {
//       System.err.println(ERR_PSTSERV_LOADING);
//       System.err.println("Cause: " + e.getMessage());
//       return;
//     }

//     this.persistenceServiceThread.start();

//     try {
//       this.remoteServiceExposer.exposeServices();
//     } catch (RemoteServicesExposerException e) {
//       System.err.println("Error: Cannot start signup service");
//       System.err.println("Cause: " + e.getMessage());
//       return;
//     }

//     this.nioListenerThread.start();
//     this.nioDispatcherThread.start();
//   }

//   /**
//    * Shuts down all the services in a graceful way.
//    * 
//    * @throws InterruptedException
//    */
//   public void shutdownServices() throws InterruptedException {
//     // nioListenerThread.interrupt();
//     // nioDispatcherThread.interrupt();

//     persistenceServiceThread.interrupt();
//     persistenceServiceThread.join();
//   }

//   // -----------------------------------------------------------------------------------------------
//   // Private classes
//   // -----------------------------------------------------------------------------------------------

//   /**
//    * A <code>PersistenceServiceTask</code> is a wrapper class for <code>PersistenceService</code>
//    * whose mainly function is to define a completion procedure for <code>PersistenceService</code>
//    * when it stops for whatever reason.
//    * 
//    * @author Yuri Caprini
//    */
//   private class PersistenceServiceTask extends FutureTask<PersistenceService> {

//     /**
//      * Constructs a new <code>PersistenceServiceTask<code>.
//      * 
//      * @throws NullPointerException if <code>persistenceService==null</code>
//      * @param persistenceService the <code>PersistenceService</code> to wrap
//      */
//     public PersistenceServiceTask(PersistenceService persistenceService) {
//       super(persistenceService);
//     }

//     @Override
//     protected void done() {

//       try {
//         this.get();
//       } catch (InterruptedException e) {
//         // catching this exception is mandatory since it's checked, but this branch is
//         // never reached: persistence service is interrupted exactly one time by shutdown
//         // hook procedure and this.get() is called after it
//       } catch (ExecutionException ee) {
//         System.err.println("PersistenceService: interrupted by " + ee.getCause());
//         persistenceService.cleanUp();
//         System.exit(-1); // calls shutdown hook
//       }
//       persistenceService.cleanUp();
//     }
//   }

//   /**
//    * A <code>ListenerTask</code> is a wrapper class for <code>Listener</code> whose mainly function
//    * is to define a shutdown procedure for <code>Listener</code> when it stops for whatever reason.
//    * 
//    * @author Yuri Caprini
//    *
//    */
//   private class NIOListenerTask extends FutureTask<NIOListener> {

//     private final NIOListener listener;

//     /**
//      * Constructs a new instance of <code>ListenerTask</code>.
//      * 
//      * @throws NullPointerException if <code>listener==null</code>
//      * @param listener the <code>PersistenceService</code> to wrap
//      */
//     public NIOListenerTask(NIOListener listener) {
//       super(listener);
//       this.listener = listener;
//     }

//     @Override
//     protected void done() {

//       try {
//         this.get(); 

//       } catch (InterruptedException ie) {
//         // catching this exception is mandatory since it's checked, but this branch is
//         // never reached: persistence service is interrupted exactly one time by shutdown
//         // hook procedure and this.get() is called after it
//       } catch (ExecutionException ee) {
//         System.err.println("Listener: interrupted by " + ee.getCause());
//         this.listener.cleanUp();
//         System.exit(-1); // calls shutdown hook
//       } 
//       this.listener.cleanUp();
//     }
//   }

//   private class NIODispatcherTask extends FutureTask<NIODispatcher> {

//     private final NIODispatcher nioDispatcher;

//     public NIODispatcherTask(NIODispatcher nioDispatcher) {
//       super(nioDispatcher);
//       this.nioDispatcher = nioDispatcher;
//     }

//     @Override
//     protected void done() {

//       try {
//         this.get(); 

//       } catch (InterruptedException ie) {
//         // catching this exception is mandatory since it's checked, but this branch is
//         // never reached: persistence service is interrupted exactly one time by shutdown
//         // hook procedure and this.get() is called after it
//       } catch (ExecutionException ee) {
//         System.err.println("Dispatcher: interrupted by " + ee.getCause());
//         try {
//           this.nioDispatcher.cleanUpResources();
//         } catch (CleanUpResourcesException e) {
//           // TODO Auto-generated catch block
//           e.printStackTrace();
//         }
//         System.exit(-1); // calls shutdown hook
//       } 
//       try {
//         this.nioDispatcher.cleanUpResources();
//       } catch (CleanUpResourcesException e) {
//         // TODO Auto-generated catch block
//         e.printStackTrace();
//       }
//     }
//   }
//   /**
//    * 
//    * 
//    * private class DispatcherFutureTask extends FutureTask<Dispatcher>{
//    * 
//    * private final Dispatcher dispatcher;
//    * 
//    * public DispatcherFutureTask(Dispatcher dispatcher) { super(dispatcher); this.dispatcher =
//    * dispatcher; }
//    * 
//    * @Override protected void done(){
//    * 
//    *           try { this.get(); //returned value ignored
//    * 
//    *           } catch (InterruptedException ie) { System.err.println("Dispatcher: interrupted"); }
//    *           catch (ExecutionException ee) { System.err.println("Dispatcher: interrupted by a " +
//    *           ee.getCause()); } finally { try { this.dispatcher.cleanUpResources(); } catch
//    *           (IOException ioe) { System.err.println("Dispatcher: unexpected exception " + "closing
//    *           resources"); } listenerThread.interrupt(); } } }
//    */
// }
