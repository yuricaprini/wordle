package io.github.yuricaprini.wordleserver.circle04frameworks;

import java.rmi.Remote;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import io.github.yuricaprini.wordleserver.circle05configurations.Server;
import io.github.yuricaprini.wordleserver.circle05configurations.Service;

/**
 * A {@code RemoteExposerService} is a {@link Service} within Wordle {@link Server} for 
 * exposing/unexposing {@link Remote} objects.
 * 
 * @author Yuri Caprini
 */
public abstract class RemoteExposerService implements Service {

  private boolean isUpAndRunning;
  private boolean isOnShutdown;
  private boolean isTerminated;
  protected Map<String, Remote> remoteObjs;

  /**
   * Abstract {@code RemoteExposerService} constructor. Its role is to enforce a minimum setup for
   * remote objects exposers constructed by subclasses. Each newly constructed remote objects 
   * exposer must have at least a map binding remote objects to their IDs.
   * 
   * @param remoteObjs the remote objects to be exposed.
   * @throws NullPointerException if {@code remoteObjs == null}
   */
  public RemoteExposerService(Hashtable<String, Remote> remoteObjs) {
    Objects.requireNonNull(remoteObjs);
    this.remoteObjs = remoteObjs;
  }

  /**
   * Exposes all remote objects at startup, and goes to sleep until {@code shutdown()} is called.
   * When this happens, it unexposes all remote objects before termination.
   * 
   * @return this instance of {@code RemoteExposerService}.
   * @throws Exception if an error occurs during service lifecycle.
   */
  @Override
  public RemoteExposerService call() throws Exception {

    initState();

    exposeAll();

    notifyAllIsUpAndRunning();

    waitIsOnShutdown();

    unexposeAll();

    notifyAllIsTerminated();

    return this;
  }

  @Override
  public String getName() {
    return "RemoteExposer";
  }

  @Override
  public void shutdown() {
    notifyAllIsOnShutdown();
  }

  @Override
  public void awaitIsUpAndRunning() throws InterruptedException {
    waitIsUpAndRunning();
  }

  @Override
  public void awaitTermination() throws InterruptedException {
    waitIsTerminated();
  }

  /**
   * Exposes all the the remote objects managed by this service.
   * 
   * @throws Exception if an error occurs during exposing.
   */
  protected abstract void exposeAll() throws Exception;

  /**
   * Unexposes all the the remote objects managed by this service.
   * 
   * @throws Exception if an error occurs during unexposing.
   */
  protected abstract void unexposeAll() throws Exception;

  private void initState() {
    this.isUpAndRunning = false;
    this.isOnShutdown = false;
    this.isTerminated = false;
  }

  private void waitIsUpAndRunning() throws InterruptedException {
    synchronized (this) {
      while (!isUpAndRunning) {
        this.wait();
      }
    }
  }

  private void notifyAllIsUpAndRunning() {
    synchronized (this) {
      isUpAndRunning = true;
      this.notifyAll();
    }
  }

  private void waitIsTerminated() throws InterruptedException {
    synchronized (this) {
      while (!isTerminated) {
        this.wait();
      }
    }
  }

  private void notifyAllIsTerminated() {
    synchronized (this) {
      isTerminated = true;
      this.notifyAll();
    }
  }

  private void waitIsOnShutdown() throws InterruptedException {
    synchronized (this) {
      while (!isOnShutdown) {
        this.wait();
      }
    }
  }

  private void notifyAllIsOnShutdown() {
    synchronized (this) {
      isOnShutdown = true;
      notifyAll();
    }
  }
}
