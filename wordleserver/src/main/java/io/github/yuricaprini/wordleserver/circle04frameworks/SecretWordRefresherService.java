package io.github.yuricaprini.wordleserver.circle04frameworks;

import io.github.yuricaprini.wordleserver.circle05configurations.Service;

public abstract class SecretWordRefresherService implements Service {

  private Long secretWordRefreshInterval;

  private Thread currentThread;
  private boolean isUpAndRunning;

  public SecretWordRefresherService(Long secretWordRefreshInterval) {
    this.secretWordRefreshInterval = secretWordRefreshInterval;
  }

  @Override
  public SecretWordRefresherService call() throws Exception {

    initState();

    this.refreshSecretWord();

    notifyAllIsUpAndRunning();

    while (!Thread.interrupted()) {

      try {
        Thread.sleep(secretWordRefreshInterval * 60 * 1000);
      } catch (InterruptedException e) {
        break;
      }

      this.refreshSecretWord();
    }

    return this;
  }

  @Override
  public String getName() {
    return "SecretWordRefresher";
  }

  @Override
  public void shutdown() {
    currentThread.interrupt();
  }

  @Override
  public void awaitIsUpAndRunning() throws InterruptedException {
    waitIsUpAndRunning();
  }

  @Override
  public void awaitTermination() throws InterruptedException {
    currentThread.join();
  }

  protected abstract void refreshSecretWord() throws Exception;

  private void initState() {
    this.currentThread = Thread.currentThread();
    this.isUpAndRunning = false;
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
}
