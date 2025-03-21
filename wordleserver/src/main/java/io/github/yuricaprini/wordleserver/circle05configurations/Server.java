package io.github.yuricaprini.wordleserver.circle05configurations;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * A {@code Server} represents the Wordle server that manages and runs multiple services 
 * concurrently. It provides methods to add services, run them, and gracefully shut them down.
 */
public class Server {

  private List<Service> runningServices;
  private ResourceBundle CLIServerMessages;
  private Thread currentThread;

  /**
   * Constructs a new {@code Server} using the builder pattern.
   *
   * @param builder the builder used to construct the server.
   */
  private Server(Builder builder) {

    this.runningServices = builder.runningServices;
    this.CLIServerMessages = builder.CLIServerMessages;
  }

  /**
   * Starts the server by launching each service in a separate thread. It waits for each service
   * to be up and running before moving on to the next one. If a service fails during execution,
   * the server is gracefully shut down, and the application exits.
   */
  public void run() {

    currentThread = Thread.currentThread();

    for (Service service : runningServices) {

      Thread serviceThread = new Thread(new FutureTask<Service>(service) {

        @Override
        protected void done() { // runs in service thread

          try {
            this.get();

          } catch (ExecutionException ee) {

            ee.printStackTrace();

            currentThread.interrupt(); // wakes up main thread (run() caller) from waiting, if alive
            runningServices.remove(service);

            System.exit(-1); // calls registered shutdown hook to stop all the other services

          } catch (InterruptedException e) {
            // branch never reached since done() is called by service thread after call()
            // completion,so get() does not have to wait and can not be interrupted while waiting
          }
        }
      });
      serviceThread.setName(service.getName());
      serviceThread.start();

      try {
        service.awaitIsUpAndRunning();
        System.out.println(
            MessageFormat.format(CLIServerMessages.getString("OUT_SERVICEUP"), service.getName()));
      } catch (InterruptedException e) {
        // this happens due to an interruption called in done() method when service fails execution while starting...
        return;
      }
    }
    System.out.println(CLIServerMessages.getString("OUT_HELLO"));
  }

  /**
  * Gracefully shuts down all the services managed by the server. This method interrupts
  * each service thread and waits for their termination in the reverse order of their addition
  * during the server building process.
  * 
  * @throws InterruptedException if the current thread is interrupted while waiting for service
  * termination.
  */
  public void shutdown() throws InterruptedException {
    System.out.println(CLIServerMessages.getString("OUT_SHUTDOWN"));

    synchronized (this.runningServices) {
      ListIterator<Service> iterator = this.runningServices.listIterator(runningServices.size());

      while (iterator.hasPrevious()) {
        Service runningService = iterator.previous();
        iterator.remove();

        runningService.shutdown();
        runningService.awaitTermination();
        System.out.println(MessageFormat.format(CLIServerMessages.getString("OUT_SERVICEDOWN"),
            runningService.getName()));
      }
    }

    System.out.println(CLIServerMessages.getString("OUT_GOODBYE"));
  }

  /**
   * Builder class for constructing instances of {@code Server}.
   */
  public static class Builder {

    List<Service> runningServices;
    ResourceBundle CLIServerMessages;

    /**
     * Constructs a new {@code Builder} with the given resource bundle.
     *
     * @param CLIServerMessages resource bundle for console server messages.
     */
    public Builder(ResourceBundle CLIServerMessages) {
      this.runningServices = Collections.synchronizedList(new ArrayList<Service>());
      this.CLIServerMessages = CLIServerMessages;
    }

    /**
     * Adds a service to the list of services to be managed by the server.
     *
     * @param service the service to add.
     * @return the builder instance.
     */
    public Builder addService(Service service) {
      this.runningServices.add(service);
      return this;
    }

    /**
     * Builds and returns an instance of {@code Server} based on the builder's configuration.
     *
     * @return the constructed server.
     */
    public Server build() {
      return new Server(this);
    }
  }
}
