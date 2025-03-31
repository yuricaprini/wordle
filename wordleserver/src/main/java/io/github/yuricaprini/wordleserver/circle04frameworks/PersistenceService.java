package io.github.yuricaprini.wordleserver.circle04frameworks;

import io.github.yuricaprini.wordleserver.circle05configurations.Service;

public abstract class PersistenceService implements Service {

  private boolean isUpAndRunning;
  private boolean isOnShutdown;
  private boolean isTerminated;

  @Override
  public Service call() throws Exception {

    initState();

    loadStatus();

    notifyAllIsUpAndRunning();

    waitIsOnShutdown();

    storeStatus();

    notifyAllIsTerminated();

    return this;
  }

  @Override
  public String getName() {
    return "Persistence";
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

  protected abstract void loadStatus() throws Exception;

  protected abstract void storeStatus() throws Exception;

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
