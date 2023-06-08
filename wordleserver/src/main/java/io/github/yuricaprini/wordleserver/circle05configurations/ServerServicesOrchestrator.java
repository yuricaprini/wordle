package io.github.yuricaprini.wordleserver.circle05configurations;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * A {@code ServerServicesOrchestrator} is responsible for managing the start-up and
 * shutdown of the main services that make up the Wordle server. It handles any exceptions that
 * occur during their execution and ensures a graceful termination of the services.
 * 
 * <p>
 * The services are added to the orchestrator using the {@link #add(ServerService)} method, and
 * they will be started in the order of their addition when {@link #runServices()} is called.
 * Similarly, when {@link #shutdownServices()} is called, the services will be gracefully shut
 * down in the reverse order of their addition.
 * </p>
 * 
 * @author Yuri Caprini
 */
public class ServerServicesOrchestrator {

  private List<Thread> runningServices;

  /**
   * Constructs a new instance of {@code ServerServicesOrchestrator}.
   */
  public ServerServicesOrchestrator() {
    this.runningServices = new ArrayList<Thread>();
  }

  /**
   * Adds a {@link ServerService} to the list of running services.
   * @param service the server service to be added
   */
  public void add(ServerService service) {
    Thread runningService = new Thread(new FutureTask<ServerService>(service) {

      private ServerService service = null;

      @Override
      protected void done() {
        try {
          System.out.println(service.getServiceName() + " " + "is closing");
          this.service = this.get();
        } catch (InterruptedException e) {
          // Do nothing, as service interruption occurs only during graceful termination,
          // and this case is already handled after the try-catch block.
        } catch (ExecutionException ee) {
          System.err.println(service.getServiceName() + " " + "interrupted unexpectedly");
          ee.printStackTrace();
          this.service.cleanupServiceResources();
          System.exit(1); // Calls shutdown hook
        }
        this.service.cleanupServiceResources();
      }
    });
    runningService.setName(service.getServiceName());
    this.runningServices.add(runningService);
  }

  /**
   * Starts all the added services in the order of their addition.
   */
  public void runServices() {

    for (Thread runningService : runningServices) {
      System.out.println(runningService.getName() + " " + "is starting");
      runningService.start();
    }
  }

  /**
   * Shuts down all the services in a graceful manner. This method interrupts each service thread
   * and waits for their termination in the reverse order of their addition.
   * 
   * @throws InterruptedException if the current thread calling this method is interrupted while
   * waiting for service termination
   */
  public void shutdownServices() throws InterruptedException {

    ListIterator<Thread> iterator = this.runningServices.listIterator(runningServices.size());
    while (iterator.hasPrevious()) {
      Thread runningService = iterator.previous();
      runningService.interrupt();
      runningService.join();
    }
  }

}
